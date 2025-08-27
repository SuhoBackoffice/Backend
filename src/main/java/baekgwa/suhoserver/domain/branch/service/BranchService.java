package baekgwa.suhoserver.domain.branch.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import baekgwa.suhoserver.domain.branch.dto.BranchResponse;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.infra.excel.parser.SheetParserHandler;
import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.branch.bom.repository.BranchBomRepository;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import baekgwa.suhoserver.model.branch.type.repository.BranchTypeRepository;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import baekgwa.suhoserver.model.version.repository.VersionInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.domain.branch.service
 * FileName    : BranchService
 * Author      : Baekgwa
 * Date        : 2025-08-05
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-05     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BranchService {

	private final BranchTypeRepository branchTypeRepository;
	private final BranchBomRepository branchBomRepository;
	private final VersionInfoRepository versionInfoRepository;

	private static final List<String> DRAWING_NUMBER_KEYS = List.of("도번");
	private static final List<String> ITEM_NAME_KEYS = List.of("품명");
	private static final List<String> QUANTITY_KEYS = List.of("수량", "원수량", "납품수량");

	@Transactional
	public BranchResponse.PostNewBranchBom createNewBranchBom(String branchCode, Long versionInfoId,
		MultipartFile file) {
		// 1. 버전 유효성 검증 및, Entity 조회
		VersionInfoEntity findVersionInfo = versionInfoRepository.findById(versionInfoId)
			.orElseThrow(
				() -> new GlobalException(ErrorCode.NOT_FOUND_VERSION));

		// 1-1. BranchTypeEntity, 동일 version, 분기번호, versionDate 업로드 금지.
		branchTypeRepository.findBranchType(findVersionInfo.getId(), branchCode, LocalDate.now())
			.ifPresent(b -> {
				throw new GlobalException(ErrorCode.ALREADY_UPLOADED_COMPLETE_BRANCH_BOM);
			});

		// 2. Branch Type 신규 생성 및 저장
		BranchTypeEntity newBranchType = BranchTypeEntity.createNewBranchType(findVersionInfo, branchCode);
		BranchTypeEntity savedBranchType = branchTypeRepository.save(newBranchType);

		// 3. multipartFile 파싱
		SheetParserHandler handler = extractExcelXml(file);

		// 4. handler 로, Entity 생성
		List<BranchBomEntity> newBranchBomList = convertToBranchBomList(handler, savedBranchType);

		// 5. 저장
		branchBomRepository.saveAll(newBranchBomList);

		return new BranchResponse.PostNewBranchBom(savedBranchType.getId());
	}

	@Transactional(readOnly = true)
	public BranchResponse.BranchInfoDto getLatestVersionBranchBom(String branchCode, Long versionInfoId) {
		// 1. branchCode, versionInfoId 로, branchType Entity 조회
		BranchTypeEntity findBranchType =
			branchTypeRepository.findLatest(versionInfoId, branchCode, PageRequest.of(0, 1))
				.stream()
				.findFirst()
				.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_BRANCH_BOM));

		// 2. branchType PK 로, branchBom List 조회
		List<BranchBomEntity> findBranchBomList = branchBomRepository.findByBranchTypeEntity(findBranchType);

		// 2-1. branchBom 이 없다면, Exception Handling
		if (findBranchBomList.isEmpty())
			throw new GlobalException(ErrorCode.NOT_FOUND_BRANCH_BOM);

		// 3. DTO Convert return
		List<BranchResponse.BranchDetailInfoDto> branchDetailInfoDtoList =
			findBranchBomList.stream().map(BranchResponse.BranchDetailInfoDto::of).toList();
		return BranchResponse.BranchInfoDto.from(findBranchType, branchDetailInfoDtoList);
	}

	@Transactional(readOnly = true)
	public List<BranchResponse.BranchDetailInfoDto> getBranchBomList(Long branchTypeId) {
		// 1. branchType Entity 조회
		BranchTypeEntity findBranchType = branchTypeRepository.findById(branchTypeId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_BRANCH_TYPE));

		// 2. 분기레일 BOM Entity List 조회
		List<BranchBomEntity> findBranchBomList = branchBomRepository.findByBranchTypeEntity(findBranchType);

		// 2-1. 만약, 찾은 bom list 가 하나도 없으면 잘못등록됨.
		if (findBranchBomList.isEmpty()) {
			throw new GlobalException(ErrorCode.NOT_FOUND_BRANCH_BOM);
		}

		// 3. 찾은 BranchBomEntity 로, 응답 객체 생성
		return findBranchBomList.stream().map(BranchResponse.BranchDetailInfoDto::of).toList();
	}

	private List<BranchBomEntity> convertToBranchBomList(SheetParserHandler handler, BranchTypeEntity savedBranchType) {
		List<List<String>> rows = handler.getRows();
		if (rows.isEmpty()) {
			throw new GlobalException(ErrorCode.INVALID_EXCEL_PARSE_ERROR);
		}

		Map<String, Integer> headerIndexMap = extractHeaderIndexMap(rows);
		if (!hasAnyHeader(headerIndexMap, DRAWING_NUMBER_KEYS)
			|| !hasAnyHeader(headerIndexMap, ITEM_NAME_KEYS)
			|| !hasAnyHeader(headerIndexMap, QUANTITY_KEYS)) {
			throw new GlobalException(ErrorCode.INVALID_EXCEL_PARSE_ERROR);
		}

		List<BranchBomEntity> newBranchBomList = new ArrayList<>();
		for (List<String> row : rows) {
			if (isDataRow(row, headerIndexMap)) {
				Map<String, String> rowMap = toRowMap(row, headerIndexMap);
				newBranchBomList.add(toBranchBomEntity(rowMap, savedBranchType));
			}
		}
		if (newBranchBomList.isEmpty()) {
			throw new GlobalException(ErrorCode.INVALID_EXCEL_PARSE_ERROR);
		}
		return newBranchBomList;
	}

	private Map<String, Integer> extractHeaderIndexMap(List<List<String>> rows) {
		for (List<String> row : rows) {
			if (row.contains("도번") && row.contains("품명")) {
				Map<String, Integer> headerMap = new HashMap<>();
				for (int i = 0; i < row.size(); i++) {
					String nowString = row.get(i);
					if (!nowString.isBlank()) {
						headerMap.put(nowString, i);
					}
				}
				return headerMap;
			}
		}
		return new HashMap<>();
	}

	private boolean isDataRow(List<String> row, Map<String, Integer> headerIndexMap) {
		if (row.contains("도번") || row.contains("품명"))
			return false;
		long emptyCount = row.stream().filter(s -> s == null || s.isBlank()).count();
		if (emptyCount == row.size())
			return false;

		// 필수 컬럼 중 실제 값이 들어있는 컬럼이 모두 존재해야 함
		if (!hasAnyValidValue(row, headerIndexMap, DRAWING_NUMBER_KEYS))
			return false;
		if (!hasAnyValidValue(row, headerIndexMap, ITEM_NAME_KEYS))
			return false;

		return hasAnyValidValue(row, headerIndexMap, QUANTITY_KEYS);
	}

	private boolean hasAnyHeader(Map<String, Integer> headerIndexMap, List<String> candidates) {
		for (String key : candidates) {
			if (headerIndexMap.containsKey(key))
				return true;
		}
		return false;
	}

	private boolean hasAnyValidValue(List<String> row, Map<String, Integer> headerIndexMap, List<String> candidates) {
		for (String col : candidates) {
			Integer idx = headerIndexMap.get(col);
			if (idx != null && idx < row.size()) {
				String val = row.get(idx);
				if (val != null && !val.isBlank())
					return true;
			}
		}
		return false;
	}

	private Map<String, String> toRowMap(List<String> row, Map<String, Integer> headerIndexMap) {
		Map<String, String> rowMap = new HashMap<>();
		for (Map.Entry<String, Integer> entry : headerIndexMap.entrySet()) {
			String key = entry.getKey();
			Integer index = entry.getValue();
			rowMap.put(key, index < row.size() ? row.get(index) : "");
		}
		return rowMap;
	}

	private String getHeaderValue(Map<String, String> rowMap, List<String> candidates) {
		for (String key : candidates) {
			String value = rowMap.get(key);
			if (value != null && !value.isBlank())
				return value;
		}
		return null;
	}

	private BranchBomEntity toBranchBomEntity(Map<String, String> rowMap, BranchTypeEntity savedBranchType) {
		String itemType = rowMap.get("품목구분");
		String drawingNumber = getHeaderValue(rowMap, DRAWING_NUMBER_KEYS);
		String itemName = getHeaderValue(rowMap, ITEM_NAME_KEYS);
		String quantityStr = getHeaderValue(rowMap, QUANTITY_KEYS);
		if (drawingNumber == null || itemName == null || quantityStr == null)
			throw new GlobalException(ErrorCode.INVALID_EXCEL_PARSE_ERROR);

		Long unitQuantity = Long.parseLong(quantityStr);
		String specification = rowMap.getOrDefault("규격", "");
		String unit = rowMap.getOrDefault("단위", "");
		Boolean suppliedMaterial = rowMap.getOrDefault("비고", "").contains("사급");

		return BranchBomEntity.builder()
			.itemType(itemType)
			.drawingNumber(drawingNumber)
			.specification(specification)
			.itemName(itemName)
			.unitQuantity(unitQuantity)
			.unit(unit)
			.suppliedMaterial(suppliedMaterial)
			.branchTypeEntity(savedBranchType)
			.build();
	}

	private SheetParserHandler extractExcelXml(MultipartFile file) {
		OPCPackage pkg;
		try {
			pkg = OPCPackage.open(file.getInputStream());
			XSSFReader xssfReader = new XSSFReader(pkg);

			InputStream sheetStream = xssfReader.getSheetsData().next();
			SharedStrings sst = xssfReader.getSharedStringsTable();
			SharedStringsTable sstTable = (SharedStringsTable)sst;

			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			saxParserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			saxParserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);

			XMLReader parser = saxParserFactory.newSAXParser().getXMLReader();
			SheetParserHandler handler = new SheetParserHandler(sstTable);
			parser.setContentHandler(handler);

			parser.parse(new InputSource(sheetStream));

			return handler;
		} catch (Exception e) {
			throw new GlobalException(ErrorCode.INVALID_EXCEL_PARSE_ERROR);
		}
	}
}
