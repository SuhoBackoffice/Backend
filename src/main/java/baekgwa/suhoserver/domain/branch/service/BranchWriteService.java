package baekgwa.suhoserver.domain.branch.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.infra.excel.parser.SheetParserHandler;
import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.branch.bom.repository.BranchBomRepository;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import baekgwa.suhoserver.model.branch.type.repository.BranchTypeRepository;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.branch.service
 * FileName    : BranchWriteService
 * Author      : Baekgwa
 * Date        : 2025-09-14
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-14     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class BranchWriteService {

	private final BranchTypeRepository branchTypeRepository;
	private final BranchBomRepository branchBomRepository;

	private static final List<String> DRAWING_NUMBER_KEYS = List.of("도번");
	private static final List<String> ITEM_NAME_KEYS = List.of("품명");
	private static final List<String> QUANTITY_KEYS = List.of("수량", "원수량", "납품수량");

	/**
	 * 새로운 분기 타입을 생성 및 저장
	 * @param file Excel Bom List 파일
	 * @param findVersionInfo 버전 PK
	 * @param branchCode 분기 코드
	 * @param imageUrl 분기 이미지 URL
	 * @return 저장된 분기레일 타입
	 */
	@Transactional
	public BranchTypeEntity saveNewBranchType(
		MultipartFile file,
		VersionInfoEntity findVersionInfo,
		String branchCode,
		String imageUrl
	) {
		String branchName = generateBranchName(file, branchCode);

		BranchTypeEntity newBranchType = BranchTypeEntity.createNewBranchType(findVersionInfo, branchCode, branchName,
			imageUrl);
		return branchTypeRepository.save(newBranchType);
	}

	/**
	 * 분기 타입과, Excel 파일로, 분기레일 BOM Entity List 생성 및 저장
	 * @param savedBranchType 분기 타입
	 * @param file 저장할 Bom List Excel
	 */
	@Transactional
	public void saveNewBranchBom(BranchTypeEntity savedBranchType, MultipartFile file) {
		// 1. file 정보로, Rows 데이터 생성
		List<List<String>> rows = parseExcelToRows(file);

		// 2. rows 정보로, BranchBomList 생성
		List<BranchBomEntity> newBranchBomList = convertToBranchBomList(rows, savedBranchType);

		// 3. 영속성 저장
		branchBomRepository.saveAll(newBranchBomList);
	}

	/**
	 * 데이터를 분기 Bom List 로 변환
	 * @param rows 모든 행 정보
	 * @param savedBranchType 참조될 분기 타입 정보
	 * @return 분기 Bom
	 */
	private List<BranchBomEntity> convertToBranchBomList(List<List<String>> rows, BranchTypeEntity savedBranchType) {
		if (rows.isEmpty()) {
			throw new GlobalException(ErrorCode.INVALID_EXCEL_PARSE_ERROR);
		}

		// 헤더 추출
		Map<String, Integer> headerIndexMap = extractHeaderIndexMapOrThrow(rows);

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

	/**
	 * 데이터를 BranchBomEntity 로 변경하는 Convert 메서드
	 * @param rowMap 데이터 행 Map
	 * @param savedBranchType 참조될 분기 타입 정보
	 * @return 분기레일 Bom Entity
	 */
	private BranchBomEntity toBranchBomEntity(Map<String, String> rowMap, BranchTypeEntity savedBranchType) {
		String itemType = rowMap.get("품목구분");
		String drawingNumber = getHeaderValue(rowMap, DRAWING_NUMBER_KEYS);
		String itemName = getHeaderValue(rowMap, ITEM_NAME_KEYS);
		String quantityStr = getHeaderValue(rowMap, QUANTITY_KEYS);

		if (drawingNumber == null || itemName == null || quantityStr == null)
			throw new GlobalException(ErrorCode.INVALID_EXCEL_PARSE_ERROR);

		// '규격'은 현재 고정된 헤더 이름을 사용.
		// 향후 '사양', 'SPEC' 등 동의어 추가 요구사항 발생 시,
		// DRAWING_NUMBER_KEYS 처럼 List와 getHeaderValue()를 사용하도록 리팩토링할 것.
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

	/**
	 * 후보 헤더명 목록(candidates)을 순서대로 조회하여 현재 행(rowMap)에서 첫 번째로 발견되는 "비어있지 않은 값"을 반환
	 * @param rowMap 행 데이터 Map
	 * @param candidates 예상 키 List
	 * @return 첫 번째 비어있지 않은 값, 없으면 null
	 */
	private String getHeaderValue(Map<String, String> rowMap, List<String> candidates) {
		for (String key : candidates) {
			String value = rowMap.get(key);
			if (value != null && !value.isBlank())
				return value;
		}
		return null;
	}

	/**
	 * 행을 헤더 맵을 사용하여, Map 으로 반환
	 * @param row 행
	 * @param headerIndexMap Header 의 인덱스 번호 Map
	 * @return Map
	 */
	private Map<String, String> toRowMap(List<String> row, Map<String, Integer> headerIndexMap) {
		Map<String, String> rowMap = new HashMap<>();
		for (Map.Entry<String, Integer> entry : headerIndexMap.entrySet()) {
			String key = entry.getKey();
			Integer index = entry.getValue();
			rowMap.put(key, index < row.size() ? row.get(index) : "");
		}
		return rowMap;
	}

	/**
	 * 데이터 행인지 확인
	 * 필수값은, "도번", "품명", "수량"
	 * @param row 행
	 * @param headerIndexMap Header 의 인덱스 번호 Map
	 * @return boolean
	 */
	private boolean isDataRow(List<String> row, Map<String, Integer> headerIndexMap) {
		if (row.contains("도번") || row.contains("품명"))
			return false;
		long emptyCount = row.stream().filter(s -> s == null || s.isBlank()).count();
		if (emptyCount == row.size())
			return false;

		if (!hasAnyValidValue(row, headerIndexMap, DRAWING_NUMBER_KEYS))
			return false;
		if (!hasAnyValidValue(row, headerIndexMap, ITEM_NAME_KEYS))
			return false;

		return hasAnyValidValue(row, headerIndexMap, QUANTITY_KEYS);
	}

	/**
	 * 필수 컬럼 중, 실제 값이 들어있는 컬럼이 존재하는지 유무 확인
	 * @param row 행
	 * @param headerIndexMap Header 의 인덱스 번호 Map
	 * @param candidates 예상 key List
	 * @return boolean
	 */
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

	/**
	 * 도번 혹은 품명이 있는 row 를 헤더 Row 로 선정
	 * 각 섹션 별, Header 로우의 인덱스 번호를 Map 으로 추출
	 * @throws GlobalException
	 * @param rows Excel Rows
	 * @return Header 로우의 인덱스 번호 Map
	 */
	private Map<String, Integer> extractHeaderIndexMapOrThrow(List<List<String>> rows) {
		for (List<String> row : rows) {
			if (row == null || !row.contains("도번") || !row.contains("품명")) {
				continue;
			}

			Map<String, Integer> headerMap = new HashMap<>();
			for (int i = 0; i < row.size(); i++) {
				String v = row.get(i);
				if (v == null) continue;
				v = v.trim();
				if (v.isEmpty()) continue;
				headerMap.put(v, i);
			}

			if (hasAnyHeader(headerMap, DRAWING_NUMBER_KEYS)
				&& hasAnyHeader(headerMap, ITEM_NAME_KEYS)
				&& hasAnyHeader(headerMap, QUANTITY_KEYS)) {
				return headerMap;
			}
		}

		// 끝까지 못찾으면, 헤더 행이 없는 이상한 rows
		throw new GlobalException(ErrorCode.INVALID_EXCEL_PARSE_ERROR);
	}

	/**
	 * Map 에서, 해당 키가 있는지 (헤더 유무) 검사
	 * @param headerIndexMap 헤더 정보 Map
	 * @param candidates 타겟 키 리스트
	 * @return boolean
	 */
	private boolean hasAnyHeader(Map<String, Integer> headerIndexMap, List<String> candidates) {
		for (String key : candidates) {
			if (headerIndexMap.containsKey(key))
				return true;
		}
		return false;
	}

	/**
	 * 파일명으로, branchName 생성
	 * 만약, 파일명이 없다면, 임시 분기 명칭으로 생성
	 * @param file Excel Bom 파일
	 * @param branchCode 분기 코드
	 * @return 생성된 분기 명칭
	 */
	private String generateBranchName(MultipartFile file, String branchCode) {
		String original = file.getOriginalFilename();
		if (!StringUtils.hasText(original)) {
			return branchCode + "번 분기";
		}

		// 경로 제거 (브라우저/OS별 경로 포함 가능)
		String base = original.replaceAll(".*[\\\\/]", "");

		// 확장자 제거
		int dot = base.lastIndexOf('.');
		String nameOnly = (dot >= 0) ? base.substring(0, dot) : base;

		// 끝의 "BOM" 제거(대소문자 무시), 양옆 공백 정리
		String stripped = nameOnly.replaceAll("(?i)\\s*BOM\\s*$", "").trim();

		// 모두 사라졌다면 원래 이름(확장자 제거본)으로 폴백
		if (stripped.isEmpty())
			stripped = nameOnly.trim();

		return stripped;
	}

	/**
	 * File 로, Excel Rows Data 추출
	 * @param file BomList Excel 파일
	 * @return Excel Rows 데이터
	 */
	private List<List<String>> parseExcelToRows(MultipartFile file) {
		try (OPCPackage pkg = OPCPackage.open(file.getInputStream())) {
			XSSFReader reader = new XSSFReader(pkg);
			try (InputStream sheet = reader.getSheetsData().next()) {
				SharedStringsTable sst = (SharedStringsTable)reader.getSharedStringsTable();

				SAXParserFactory f = SAXParserFactory.newInstance();
				f.setFeature("http://xml.org/sax/features/external-general-entities", false);
				f.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
				f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
				f.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);

				XMLReader parser = f.newSAXParser().getXMLReader();
				SheetParserHandler handler = new SheetParserHandler(sst);
				parser.setContentHandler(handler);
				parser.parse(new InputSource(sheet));

				return handler.getRows();
			}
		} catch (Exception e) {
			throw new GlobalException(ErrorCode.INVALID_EXCEL_PARSE_ERROR);
		}
	}
}
