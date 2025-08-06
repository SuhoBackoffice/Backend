package baekgwa.suhoserver.domain.branch.service;

import java.io.InputStream;
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

	@Transactional
	public void createNewBranchBom(String branchCode, Long versionInfoId, MultipartFile file) {
		// 1. 버전 유효성 검증 및, Entity 조회
		VersionInfoEntity findVersionInfo = versionInfoRepository.findById(versionInfoId)
			.orElseThrow(
				() -> new GlobalException(ErrorCode.NOT_FOUND_VERSION));

		// 2. Branch Type 신규 생성 및 저장
		BranchTypeEntity newBranchType = BranchTypeEntity.createNewBranchType(findVersionInfo, branchCode);
		BranchTypeEntity savedBranchType = branchTypeRepository.save(newBranchType);

		// 3. multipartFile 파싱
		SheetParserHandler handler = extractExcelXml(file);

		// 4. handler 로, Entity 생성
		List<BranchBomEntity> newBranchBomList = convertToBranchBomList(handler, savedBranchType);

		// 5. 저장
		branchBomRepository.saveAll(newBranchBomList);
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

	private List<BranchBomEntity> convertToBranchBomList(SheetParserHandler handler, BranchTypeEntity savedBranchType) {
		List<List<String>> rows = handler.getRows();
		if (rows.isEmpty()) {
			throw new GlobalException(ErrorCode.INVALID_EXCEL_PARSE_ERROR);
		}

		Map<String, Integer> headerIndexMap = extractHeaderIndexMap(rows);
		if (headerIndexMap.isEmpty()) {
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
		// Header row 은 Data row 가 아님
		if (row.contains("도번") || row.contains("품명"))
			return false;

		// 전부 다 비워져 있는 row 도 data 영역이 아님
		long emptyCount = row.stream().filter(s -> s == null || s.isBlank()).count();
		if (emptyCount == row.size())
			return false;

		// 도번, 품명, 수량은 필수로 해당 인덱스 값이 비워져 있다면 data row 로 판명하지 않음
		List<String> requiredCols = List.of("도번", "품명", "수량");
		for (String col : requiredCols) {
			Integer idx = headerIndexMap.get(col);
			if (idx == null || idx >= row.size())
				return false;
			String val = row.get(idx);
			if (val == null || val.isBlank())
				return false;
		}
		return true;
	}

	private Map<String, String> toRowMap(List<String> row, Map<String, Integer> headerIndexMap) {
		Map<String, String> rowMap = new HashMap<>();
		for (Map.Entry<String, Integer> entry : headerIndexMap.entrySet()) {
			String key = entry.getKey();
			Integer index = entry.getValue();
			rowMap.put(key, row.get(index));
		}
		return rowMap;
	}

	private BranchBomEntity toBranchBomEntity(Map<String, String> rowMap, BranchTypeEntity savedBranchType) {
		String itemType = rowMap.get("품목구분");
		String drawingNumber = rowMap.get("도번");
		String itemName = rowMap.get("품명");
		String specification = rowMap.get("규격");
		Long unitQuantity = Long.parseLong(rowMap.get("수량"));
		String unit = rowMap.get("단위");
		Boolean suppliedMaterial = rowMap.get("비고").contains("사급");
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

			// 첫 번째 시트 가져오기
			InputStream sheetStream = xssfReader.getSheetsData().next();

			// 공통 문자열 테이블 가져오기
			SharedStrings sst = xssfReader.getSharedStringsTable();
			SharedStringsTable sstTable = (SharedStringsTable)sst;

			// 파서 및
			// 보안 옵션 적용
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			saxParserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			saxParserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);

			XMLReader parser = saxParserFactory.newSAXParser().getXMLReader();
			SheetParserHandler handler = new SheetParserHandler(sstTable);
			parser.setContentHandler(handler);

			// 파싱 후, handler return
			parser.parse(new InputSource(sheetStream));

			return handler;
		} catch (Exception e) {
			throw new GlobalException(ErrorCode.INVALID_EXCEL_PARSE_ERROR);
		}
	}
}
