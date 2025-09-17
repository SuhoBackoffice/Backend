package baekgwa.suhoserver.domain.project.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.project.dto.ProjectResponse;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.infra.download.ImageDownloader;
import baekgwa.suhoserver.infra.excel.util.ExcelMerges;
import baekgwa.suhoserver.infra.excel.util.ExcelPalette;
import baekgwa.suhoserver.infra.excel.util.ExcelRowWriter;
import baekgwa.suhoserver.infra.excel.util.ExcelStyler;
import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.branch.bom.repository.BranchBomRepository;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import baekgwa.suhoserver.model.project.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.repository.ProjectBranchRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.project.straight.repository.ProjectStraightRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.service
 * FileName    : ProjectBomService
 * Author      : Baekgwa
 * Date        : 2025-09-17
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-17     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class ProjectBomService {
	private final ProjectBranchRepository projectBranchRepository;
	private final ProjectStraightRepository projectStraightRepository;
	private final BranchBomRepository branchBomRepository;

	private final ImageDownloader imageDownloader;

	private static final int STRAIGHT_COL_KIND = 0;
	private static final int STRAIGHT_COL_LEN = 1;
	private static final int STRAIGHT_COL_TYPE = 2;
	private static final int STRAIGHT_COL_PROC = 3;
	private static final int STRAIGHT_COL_NUM = 4;
	private static final int STRAIGHT_COL_QTY = 5;
	private static final int STRAIGHT_LAST_COL = 11;
	private static final int BRANCH_LAST_COL = 24;
	private static final int BRANCH_BOM_MIN_RAW = 10;
	private static final int BRANCH_BOM_HEADER_RAW = 2;

	@Transactional(readOnly = true)
	public ProjectResponse.ProjectQuantityList getProjectQuantityList(ProjectEntity findProject) {
		// 1. 파일명 생성 및 인코딩 처리
		String fileName = "[" + findProject.getRegion() + "]" + findProject.getName() + "_" + LocalDate.now() + ".xlsx";
		String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "_");

		// 2. WorkSheet 생성
		Workbook workbook = new SXSSFWorkbook(200);

		// 3. 직선레일 물량리스트 제작
		createStraightRailWorkSheet(workbook, findProject);

		// 4. 분기레일 물량리스트 제작
		createBranchRailWorkSheet(workbook, findProject);

		// 5. 설치자재 물량리스트 제작

		// 6. 응답
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		return new ProjectResponse.ProjectQuantityList(outputStream.toByteArray(), encodedFileName);
	}

	// 분기레일 물량리스트 생성
	private void createBranchRailWorkSheet(Workbook workbook, ProjectEntity findProject) {
		Sheet sheet = workbook.createSheet("분기레일");

		// 열 사이즈 설정
		ExcelRowWriter.setColumnWidthsChars(sheet, 18, 30, 10, 40, 20, 7, 4, 40, 20, 7, 4, 40, 20, 7, 4, 25, 7, 4, 0, 0,
			0, 35, 20, 7, 4);

		// 데이터 로드
		List<ProjectBranchEntity> findProjectBranchList = projectBranchRepository.findByProjectOrderByBranchCode(
			findProject);

		int rowIdx = 0;
		rowIdx = generateBranchTitle(sheet, rowIdx, generateTitleByProjectName(findProject));
		rowIdx = generateBranchHeader(sheet, rowIdx);
		rowIdx = generateBranchBom(sheet, rowIdx, findProjectBranchList);

		// 셀 위치 고정 적용
		sheet.createFreezePane(0, 3, 0, 3);

		// 셀 마지막 줄 굵게 처리
		ExcelStyler.lineOuter(sheet.getRow(rowIdx - 1), 0, BRANCH_LAST_COL, BorderStyle.MEDIUM, false, true, false,
			false);
	}

	// 분기레일 물량리스트 생성
	private int generateBranchBom(Sheet sheet, int rowIdx, List<ProjectBranchEntity> findProjectBranchList) {

		for (ProjectBranchEntity findProjectBranch : findProjectBranchList) {
			// 1. bomList 조회
			BranchTypeEntity findBranchInfo = findProjectBranch.getBranchType();
			List<BranchBomEntity> findBranchBomList = branchBomRepository.findByBranchTypeEntity(findBranchInfo);

			// 2. 해당 분기레일의 BOM List 생성
			rowIdx = generateBranchBomParts(sheet, rowIdx, findProjectBranch, findBranchBomList);
		}

		return rowIdx;
	}

	// 분기레일 별, BOM 리스트 생성
	private int generateBranchBomParts(Sheet sheet, int rowIdx, ProjectBranchEntity findProjectBranch,
		List<BranchBomEntity> findBranchBomList) {
		// 1-1. 파트별 자재 분리 진행
		List<BranchBomEntity> upperBaseList = extractUpperBaseList(findBranchBomList);
		List<BranchBomEntity> upperGuideList = extractGuideList(findBranchBomList);
		List<BranchBomEntity> rBaseList = extractRBaseList(findBranchBomList);
		List<BranchBomEntity> stRailList = extractStRailList(findBranchBomList);
		List<BranchBomEntity> lWSPTList = extractLitzWireList(findBranchBomList);

		// 1-2. 해당 분기의 최대 row 와, 추출된 총합 개수 확인
		int rowSizeMax = Stream.of(upperBaseList, upperGuideList, rBaseList, stRailList, lWSPTList)
			.mapToInt(List::size)
			.max()
			.orElse(0);

		int rowSum = Stream.of(upperBaseList, upperGuideList, rBaseList, stRailList, lWSPTList)
			.mapToInt(List::size)
			.sum();

		// 1-3. 용도별로 분리한 BOM List 수량과, 원본 List 개수 검증. 더 많으면 잘못 추출됨.
		if (rowSum > findBranchBomList.size()) {
			throw new GlobalException(ErrorCode.CREATE_QUANTITY_LIST_BRANCH_BOM_VALID_FAIL);
		}

		int endRow = rowIdx + BRANCH_BOM_HEADER_RAW + Math.max(rowSizeMax, BRANCH_BOM_MIN_RAW) - 1;

		// 1번라인 처리
		Row bomHeaderRow1 = ExcelRowWriter.writeRow(sheet, rowIdx++, r -> {
			r.createCell(0).setCellValue(findProjectBranch.getBranchType().getCode());
			r.createCell(1).setCellValue("분기 명칭");
			r.createCell(2).setCellValue(findProjectBranch.getTotalQuantity());
			r.createCell(3).setCellValue("상판");
			r.createCell(7).setCellValue("Guide");
			r.createCell(11).setCellValue("R-BASE");
			r.createCell(15).setCellValue("레일");
			r.createCell(18).setCellValue("컷팅 LW SPT");
			r.createCell(21).setCellValue("가공 LW SPT");
		});

		// 1번라인 스타일 처리
		ExcelStyler.fontSize(bomHeaderRow1, ExcelStyler.H4);
		ExcelStyler.fontSize(bomHeaderRow1.getCell(0), ExcelStyler.H1);
		ExcelStyler.fontSize(bomHeaderRow1.getCell(2), ExcelStyler.H1);
		ExcelRowWriter.setRowHeightPx(bomHeaderRow1, ExcelStyler.H4_HEIGHT_PX);
		ExcelStyler.backgroundColor(bomHeaderRow1, 1, BRANCH_LAST_COL, ExcelPalette.DATA_BLUE);
		ExcelStyler.backgroundColor(bomHeaderRow1.getCell(0), ExcelPalette.HEADER_YELLOW);
		ExcelStyler.align(bomHeaderRow1, 0, BRANCH_LAST_COL, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
		ExcelStyler.bold(bomHeaderRow1, 0, BRANCH_LAST_COL, true);
		ExcelStyler.lineOuter(bomHeaderRow1, 0, BRANCH_LAST_COL, BorderStyle.THIN, true, true, true, true);
		ExcelStyler.lineOuter(bomHeaderRow1.getCell(0), BorderStyle.MEDIUM, false, false, true, false);
		ExcelStyler.lineOuter(bomHeaderRow1.getCell(BRANCH_LAST_COL), BorderStyle.MEDIUM, false, false, false, true);
		ExcelStyler.lineOuter(bomHeaderRow1, 0, BRANCH_LAST_COL, BorderStyle.DOUBLE, true, false, false, false);
		ExcelStyler.lineOuter(bomHeaderRow1.getCell(2), BorderStyle.DOUBLE, false, false, false, true);

		// 1번라인 병합 처리
		ExcelMerges.mergeRows(sheet, rowIdx - 1, endRow, 0);
		ExcelMerges.mergeRows(sheet, rowIdx - 1, endRow, 2);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 3, 6);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 7, 10);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 11, 14);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 15, 17);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 18, 20);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 21, 24);

		// 2번라인 처리
		Row bomHeaderRow2 = ExcelRowWriter.writeRow(sheet, rowIdx++, r -> {
			r.createCell(1).setCellValue(findProjectBranch.getBranchType().getName());
			r.createCell(3).setCellValue("품명");
			r.createCell(4).setCellValue("도번");
			r.createCell(5).setCellValue("수량");
			r.createCell(7).setCellValue("품명");
			r.createCell(8).setCellValue("도번");
			r.createCell(9).setCellValue("수량");
			r.createCell(11).setCellValue("품명");
			r.createCell(12).setCellValue("도번");
			r.createCell(13).setCellValue("수량");
			r.createCell(15).setCellValue("종류(길이)");
			r.createCell(16).setCellValue("수량");
			r.createCell(18).setCellValue("종류(길이)");
			r.createCell(19).setCellValue("수량");
			r.createCell(21).setCellValue("품명");
			r.createCell(22).setCellValue("도번");
			r.createCell(23).setCellValue("수량");
		});

		// 2번라인 스타일 처리
		ExcelStyler.fontSize(bomHeaderRow2, ExcelStyler.H5);
		ExcelRowWriter.setRowHeightPx(bomHeaderRow2, ExcelStyler.H5_HEIGHT_PX);
		ExcelStyler.backgroundColor(bomHeaderRow2, 3, BRANCH_LAST_COL, ExcelPalette.DATA_BLUE);
		ExcelStyler.backgroundColor(bomHeaderRow2.getCell(0), ExcelPalette.HEADER_YELLOW);
		ExcelStyler.align(bomHeaderRow2, 0, BRANCH_LAST_COL, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
		ExcelStyler.bold(bomHeaderRow2, 0, BRANCH_LAST_COL, true);
		ExcelStyler.lineOuter(bomHeaderRow2, 0, BRANCH_LAST_COL, BorderStyle.THIN, true, true, true, true);
		ExcelStyler.lineOuter(bomHeaderRow2.getCell(2), BorderStyle.DOUBLE, false, false, false, true);
		ExcelStyler.lineOuter(bomHeaderRow2.getCell(0), BorderStyle.MEDIUM, false, false, true, false);
		ExcelStyler.lineOuter(bomHeaderRow2.getCell(BRANCH_LAST_COL), BorderStyle.MEDIUM, false, false, false, true);

		// 2번라인 병합 처리
		ExcelMerges.mergeRows(sheet, rowIdx - 1, rowIdx - 1 + 2, 1);
		ExcelMerges.mergeRows(sheet, rowIdx - 1 + 3, endRow, 1);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 5, 6);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 9, 10);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 13, 14);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 16, 17);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 19, 20);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 23, 24);

		// 분기레일 이미지 추가
		insertImage(sheet, rowIdx + 2, 1, findProjectBranch.getBranchType().getImageUrl());

		// 3번라인 부터 데이터 영역 설정 없음 색상 처리
		ExcelStyler.backgroundColor(sheet, new CellRangeAddress(rowIdx, endRow, 3, 24), ExcelPalette.EMPTY_GRAY);

		// 3번라인 부터, 데이터 삽입 시작
		for (int i = 0; i < Math.max(rowSizeMax, BRANCH_BOM_MIN_RAW); i++) {
			BranchBomEntity upperBaseBom = (i < upperBaseList.size()) ? upperBaseList.get(i) : null;
			BranchBomEntity upperGuideBom = (i < upperGuideList.size()) ? upperGuideList.get(i) : null;
			BranchBomEntity rBaseBom = (i < rBaseList.size()) ? rBaseList.get(i) : null;
			BranchBomEntity stRailBom = (i < stRailList.size()) ? stRailList.get(i) : null;
			BranchBomEntity litzWireSPTBom = (i < lWSPTList.size()) ? lWSPTList.get(i) : null;

			final int nowRowIdx = rowIdx;
			Row bomDataRow = ExcelRowWriter.writeRow(sheet, rowIdx++, r -> {
				if (upperBaseBom != null) {
					r.createCell(3).setCellValue(upperBaseBom.getItemName());
					r.createCell(4).setCellValue(upperBaseBom.getDrawingNumber());
					r.createCell(5)
						.setCellValue((upperBaseBom.getUnitQuantity() * findProjectBranch.getTotalQuantity())
							+ upperBaseBom.getUnit());
					r.createCell(6).setCellValue("☐");
					ExcelStyler.backgroundColor(sheet, new CellRangeAddress(nowRowIdx, nowRowIdx, 3, 6),
						ExcelPalette.WHITE);
				}

				if (upperGuideBom != null) {
					r.createCell(7).setCellValue(upperGuideBom.getItemName());
					r.createCell(8).setCellValue(upperGuideBom.getDrawingNumber());
					r.createCell(9)
						.setCellValue((upperGuideBom.getUnitQuantity() * findProjectBranch.getTotalQuantity())
							+ upperGuideBom.getUnit());
					r.createCell(10).setCellValue("☐");
					ExcelStyler.backgroundColor(sheet, new CellRangeAddress(nowRowIdx, nowRowIdx, 7, 10),
						ExcelPalette.WHITE);
				}

				if (rBaseBom != null) {
					r.createCell(11).setCellValue(rBaseBom.getItemName());
					r.createCell(12).setCellValue(rBaseBom.getDrawingNumber());
					r.createCell(13)
						.setCellValue(
							(rBaseBom.getUnitQuantity() * findProjectBranch.getTotalQuantity()) + rBaseBom.getUnit());
					r.createCell(14).setCellValue("☐");
					ExcelStyler.backgroundColor(sheet, new CellRangeAddress(nowRowIdx, nowRowIdx, 11, 14),
						ExcelPalette.WHITE);
				}

				if (stRailBom != null) {
					r.createCell(15).setCellValue(stRailBom.getItemName());
					r.createCell(16)
						.setCellValue(
							(stRailBom.getUnitQuantity() * findProjectBranch.getTotalQuantity()) + stRailBom.getUnit());
					r.createCell(17).setCellValue("☐");
					ExcelStyler.backgroundColor(sheet, new CellRangeAddress(nowRowIdx, nowRowIdx, 15, 17),
						ExcelPalette.WHITE);
				}

				if (litzWireSPTBom != null) {
					r.createCell(21).setCellValue(litzWireSPTBom.getItemName());
					r.createCell(22).setCellValue(litzWireSPTBom.getDrawingNumber());
					r.createCell(23)
						.setCellValue((litzWireSPTBom.getUnitQuantity() * findProjectBranch.getTotalQuantity())
							+ litzWireSPTBom.getUnit());
					r.createCell(24).setCellValue("☐");
					ExcelStyler.backgroundColor(sheet, new CellRangeAddress(nowRowIdx, nowRowIdx, 21, 24),
						ExcelPalette.WHITE);
				}
			});

			// 데이터 영역 스타일
			ExcelStyler.fontSize(bomDataRow, ExcelStyler.P1);
			ExcelRowWriter.setRowHeightPx(bomDataRow, ExcelStyler.P1_HEIGHT_PX);
			ExcelStyler.align(bomDataRow, 0, BRANCH_LAST_COL, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
			ExcelStyler.lineOuter(bomDataRow, 0, BRANCH_LAST_COL, BorderStyle.THIN, true, true, true, true);
			ExcelStyler.lineOuter(bomDataRow.getCell(0), BorderStyle.MEDIUM, false, false, true, false);
			ExcelStyler.lineOuter(bomDataRow.getCell(BRANCH_LAST_COL), BorderStyle.MEDIUM, false, false, false, true);
			ExcelStyler.lineOuter(bomDataRow.getCell(2), BorderStyle.DOUBLE, false, false, false, true);
		}

		// 3번 row index 처리
		rowIdx = endRow + 1;
		return rowIdx;
	}

	// 전체 Bom List 에서, Upper base 관련된 데이터만 추출
	private List<BranchBomEntity> extractUpperBaseList(List<BranchBomEntity> findBranchBomList) {
		return findBranchBomList.stream()
			.filter(bom -> bom.getItemName()
				.toUpperCase()
				.replace("_", " ")
				.contains("UPPER BASE"))
			.toList();
	}

	// 전체 Bom List 에서, Guide 관련된 데이터만 추출
	private List<BranchBomEntity> extractGuideList(List<BranchBomEntity> findBranchBomList) {
		return findBranchBomList.stream()
			.filter(bom -> bom.getItemName()
				.toUpperCase()
				.contains("GUIDE"))
			.toList();
	}

	// 전체 Bom List 에서, R-BASE 관련된 데이터만 추출
	private List<BranchBomEntity> extractRBaseList(List<BranchBomEntity> findBranchBomList) {
		return findBranchBomList.stream()
			.filter(bom -> {
				String itemNameUpper = bom.getItemName().toUpperCase(); // 대소문자 구분을 없애기 위해 대문자로 변환
				return itemNameUpper.contains("BASE") && !itemNameUpper.contains("UPPER");
			})
			.toList();
	}

	// ST RAIL 만 추출
	private List<BranchBomEntity> extractStRailList(List<BranchBomEntity> findBranchBomList) {
		return findBranchBomList.stream()
			.filter(bom -> bom.getItemName()
				.toUpperCase()
				.replace("_", " ")
				.contains("ST RAIL"))
			.toList();
	}

	// LITZ WIRE 추출
	public List<BranchBomEntity> extractLitzWireList(List<BranchBomEntity> findBranchBomList) {
		return findBranchBomList.stream()
			.filter(bom -> {
				String normalizedItemName = bom.getItemName().toUpperCase().replace("_", " ");
				return normalizedItemName.contains("LITZ WIRE") || normalizedItemName.contains("SPT");
			})
			.toList();
	}

	private void createStraightRailWorkSheet(Workbook workbook, ProjectEntity findProject) {
		Sheet sheet = workbook.createSheet("직선레일");

		// 열 너비(A~L) 고정
		ExcelRowWriter.setColumnWidthsChars(sheet, 8, 8, 10, 10, 18, 8, 14, 14, 14, 14, 14, 14);

		// 데이터 로드
		List<ProjectStraightEntity> normalList = projectStraightRepository.findSortedWithType(findProject,
			Boolean.FALSE);
		List<ProjectStraightEntity> loopList = projectStraightRepository.findSortedWithType(findProject, Boolean.TRUE);

		// 헤더, 직선레일, 루프레일 순서대로 생성
		int rowIdx = 0;
		rowIdx = generateStraightTitle(sheet, rowIdx, generateTitleByProjectName(findProject));
		rowIdx = generateStraightHeader(sheet, rowIdx);
		rowIdx = generateStraightNormal(rowIdx, normalList, sheet);
		rowIdx = generateStraightLoop(rowIdx, loopList, sheet);

		// 필터 적용
		sheet.setAutoFilter(new CellRangeAddress(1, Math.max(0, rowIdx - 1), 0, STRAIGHT_LAST_COL));

		// 셀 위치 고정 적용
		sheet.createFreezePane(0, 1);
	}

	private String generateTitleByProjectName(ProjectEntity findProject) {
		return "[" + findProject.getRegion() + "] " + findProject.getName() + " 물량리스트";
	}

	private int generateStraightLoop(int rowIdx, List<ProjectStraightEntity> loopList, Sheet sheet) {
		// 루프 섹션
		int loopStart = rowIdx;
		boolean paintedFirstLoop = false;

		for (ProjectStraightEntity e : loopList) {
			Row row = ExcelRowWriter.writeRow(sheet, rowIdx++, r -> {
				r.createCell(STRAIGHT_COL_KIND).setCellValue("LOOP");
				r.createCell(STRAIGHT_COL_LEN).setCellValue(e.getLength());
				r.createCell(STRAIGHT_COL_TYPE).setCellValue(e.getStraightType().getType());
				r.createCell(STRAIGHT_COL_PROC).setCellValue(String.valueOf(e.getStraightInfo().getHolePosition()));
				r.createCell(STRAIGHT_COL_NUM)
					.setCellValue("LR-" + e.getLength() + "-" + e.getStraightType().getType());
				r.createCell(STRAIGHT_COL_QTY).setCellValue(e.getTotalQuantity());
				r.createCell(6).setCellValue(toDouble(e.getStraightInfo().getLitzwire1()));
				r.createCell(7).setCellValue(toDouble(e.getStraightInfo().getLitzwire2()));
				r.createCell(8).setCellValue(toDouble(e.getStraightInfo().getLitzwire3()));
				r.createCell(9).setCellValue(toDouble(e.getStraightInfo().getLitzwire4()));
				r.createCell(10).setCellValue(toDouble(e.getStraightInfo().getLitzwire5()));
				r.createCell(11).setCellValue(toDouble(e.getStraightInfo().getLitzwire6()));
			});

			ExcelStyler.fontSize(row, ExcelStyler.P1);
			ExcelRowWriter.setRowHeightPx(row, ExcelStyler.P1_HEIGHT_PX);
			ExcelStyler.align(row, 0, STRAIGHT_LAST_COL, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
			ExcelStyler.lineOuter(row, 0, STRAIGHT_LAST_COL, BorderStyle.THIN, true, true, true, true);
			ExcelStyler.lineOuter(row.getCell(0), BorderStyle.MEDIUM, false, false, true, false);
			ExcelStyler.lineOuter(row.getCell(STRAIGHT_LAST_COL), BorderStyle.MEDIUM, false, false, false, true);
			ExcelStyler.backgroundColor(row.getCell(STRAIGHT_COL_NUM), ExcelPalette.DATA_BLUE);
			for (int c = 6; c <= 11; c++)
				ExcelStyler.backgroundColor(row.getCell(c), ExcelPalette.DATA_BLUE);

			if (!paintedFirstLoop) {
				ExcelStyler.backgroundColor(row.getCell(STRAIGHT_COL_KIND), ExcelPalette.HEADER_YELLOW);
				ExcelStyler.bold(row.getCell(STRAIGHT_COL_KIND), true);
				paintedFirstLoop = true;
			}
		}

		// 루프 합계
		Row loopSum = ExcelRowWriter.writeRow(sheet, rowIdx++, r ->
			r.createCell(STRAIGHT_COL_QTY).setCellValue(
				loopList.stream().mapToLong(ProjectStraightEntity::getTotalQuantity).sum()));
		ExcelRowWriter.ensureCells(loopSum, 0, STRAIGHT_LAST_COL);
		ExcelStyler.fontSize(loopSum, ExcelStyler.P1);
		ExcelRowWriter.setRowHeightPx(loopSum, ExcelStyler.P1_HEIGHT_PX);
		ExcelStyler.backgroundColor(loopSum, 0, STRAIGHT_LAST_COL, ExcelPalette.HEADER_YELLOW);
		ExcelStyler.align(loopSum, 0, STRAIGHT_LAST_COL, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
		ExcelStyler.lineOuter(loopSum, 0, STRAIGHT_LAST_COL, BorderStyle.THIN, true, true, true, true);
		ExcelStyler.lineOuter(loopSum, BorderStyle.MEDIUM, false, true, false, false);
		ExcelStyler.lineOuter(loopSum.getCell(0), BorderStyle.MEDIUM, false, false, true, false);
		ExcelStyler.lineOuter(loopSum.getCell(STRAIGHT_LAST_COL), BorderStyle.MEDIUM, false, false, false, true);
		ExcelStyler.bold(loopSum, 0, STRAIGHT_LAST_COL, true);

		// 루프 병합
		if (!loopList.isEmpty()) {
			ExcelMerges.mergeRows(sheet, loopStart, rowIdx - 1, 0);
		}
		return rowIdx;
	}

	private int generateStraightNormal(int rowIdx, List<ProjectStraightEntity> normalList, Sheet sheet) {
		// 일반 레일 섹션
		int normalStart = rowIdx;
		boolean paintedFirstNormal = false;

		for (ProjectStraightEntity e : normalList) {
			Row row = ExcelRowWriter.writeRow(sheet, rowIdx++, r -> {
				r.createCell(STRAIGHT_COL_KIND).setCellValue("일반");
				r.createCell(STRAIGHT_COL_LEN).setCellValue(e.getLength());
				r.createCell(STRAIGHT_COL_TYPE).setCellValue(e.getStraightType().getType());
				r.createCell(STRAIGHT_COL_PROC).setCellValue("");
				r.createCell(STRAIGHT_COL_NUM)
					.setCellValue("SR-" + e.getLength() + "-" + e.getStraightType().getType());
				r.createCell(STRAIGHT_COL_QTY).setCellValue(e.getTotalQuantity());
				r.createCell(6).setCellValue(toDouble(e.getStraightInfo().getLitzwire1()));
				r.createCell(7).setCellValue(toDouble(e.getStraightInfo().getLitzwire2()));
				r.createCell(8).setCellValue(toDouble(e.getStraightInfo().getLitzwire3()));
				r.createCell(9).setCellValue(toDouble(e.getStraightInfo().getLitzwire4()));
				r.createCell(10).setCellValue(toDouble(e.getStraightInfo().getLitzwire5()));
				r.createCell(11).setCellValue(toDouble(e.getStraightInfo().getLitzwire6()));
			});

			ExcelStyler.fontSize(row, ExcelStyler.P1);
			ExcelRowWriter.setRowHeightPx(row, ExcelStyler.P1_HEIGHT_PX);
			ExcelStyler.align(row, 0, STRAIGHT_LAST_COL, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
			ExcelStyler.lineOuter(row, 0, STRAIGHT_LAST_COL, BorderStyle.THIN, true, true, true, true);
			ExcelStyler.lineOuter(row.getCell(0), BorderStyle.MEDIUM, false, false, true, false);
			ExcelStyler.lineOuter(row.getCell(STRAIGHT_LAST_COL), BorderStyle.MEDIUM, false, false, false, true);
			ExcelStyler.backgroundColor(row.getCell(STRAIGHT_COL_NUM), ExcelPalette.DATA_BLUE);
			for (int c = 6; c <= 11; c++)
				ExcelStyler.backgroundColor(row.getCell(c), ExcelPalette.DATA_BLUE);

			if (!paintedFirstNormal) {
				ExcelStyler.backgroundColor(row.getCell(STRAIGHT_COL_KIND), ExcelPalette.HEADER_YELLOW);
				ExcelStyler.bold(row.getCell(STRAIGHT_COL_KIND), true);
				paintedFirstNormal = true;
			}
		}

		Row normalSum = ExcelRowWriter.writeRow(sheet, rowIdx++, r ->
			r.createCell(STRAIGHT_COL_QTY)
				.setCellValue(normalList.stream().mapToLong(ProjectStraightEntity::getTotalQuantity).sum())
		);
		ExcelStyler.fontSize(normalSum, ExcelStyler.P1);
		ExcelRowWriter.setRowHeightPx(normalSum, ExcelStyler.P1_HEIGHT_PX);
		ExcelRowWriter.ensureCells(normalSum, 0, STRAIGHT_LAST_COL);
		ExcelStyler.backgroundColor(normalSum, 0, STRAIGHT_LAST_COL, ExcelPalette.HEADER_YELLOW);
		ExcelStyler.align(normalSum, 0, STRAIGHT_LAST_COL, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
		ExcelStyler.lineOuter(normalSum, 0, STRAIGHT_LAST_COL, BorderStyle.THIN, true, true, true, true);
		ExcelStyler.lineOuter(normalSum.getCell(STRAIGHT_LAST_COL), BorderStyle.MEDIUM, false, false, false, true);
		ExcelStyler.lineOuter(normalSum.getCell(0), BorderStyle.MEDIUM, false, false, true, false);
		ExcelStyler.bold(normalSum, 0, STRAIGHT_LAST_COL, true);

		// 일반 "종류" 병합
		if (!normalList.isEmpty()) {
			ExcelMerges.mergeRows(sheet, normalStart, rowIdx - 1, 0);
		}
		return rowIdx;
	}

	private int generateBranchTitle(Sheet sheet, int rowIdx, String title) {
		// 1. Title
		Row titleRow = ExcelRowWriter.writeRow(sheet, rowIdx++, r -> r.createCell(0).setCellValue(title));

		// 1-1 Title Style
		ExcelStyler.fontSize(titleRow, ExcelStyler.H1);
		ExcelRowWriter.setRowHeightPx(titleRow, ExcelStyler.H1_HEIGHT_PX);
		ExcelStyler.backgroundColor(titleRow, 0, BRANCH_LAST_COL, ExcelPalette.HEADER_GREEN);
		ExcelStyler.align(titleRow, 0, BRANCH_LAST_COL, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
		ExcelStyler.lineOuter(titleRow, 0, BRANCH_LAST_COL, BorderStyle.THIN, true, true, true, true);
		ExcelStyler.lineOuter(titleRow, 0, BRANCH_LAST_COL, BorderStyle.MEDIUM, true, false, false, false);
		ExcelStyler.lineOuter(titleRow.getCell(0), BorderStyle.MEDIUM, false, false, true, false);
		ExcelStyler.lineOuter(titleRow.getCell(BRANCH_LAST_COL), BorderStyle.MEDIUM, false, false, false, true);
		ExcelStyler.bold(titleRow, 0, BRANCH_LAST_COL, true);

		// 1-2 셀 merge
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 0, BRANCH_LAST_COL);
		return rowIdx;
	}

	private int generateStraightTitle(Sheet sheet, int rowIdx, String title) {
		// 1. Title
		Row titleRow = ExcelRowWriter.writeRow(sheet, rowIdx++, r -> r.createCell(0).setCellValue(title));

		// 1-1 Title Style
		ExcelStyler.fontSize(titleRow, ExcelStyler.H1);
		ExcelRowWriter.setRowHeightPx(titleRow, ExcelStyler.H1_HEIGHT_PX);
		ExcelStyler.backgroundColor(titleRow, 0, STRAIGHT_LAST_COL, ExcelPalette.HEADER_GREEN);
		ExcelStyler.align(titleRow, 0, STRAIGHT_LAST_COL, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
		ExcelStyler.lineOuter(titleRow, 0, STRAIGHT_LAST_COL, BorderStyle.THIN, true, true, true, true);
		ExcelStyler.lineOuter(titleRow, 0, STRAIGHT_LAST_COL, BorderStyle.MEDIUM, true, false, false, false);
		ExcelStyler.lineOuter(titleRow.getCell(0), BorderStyle.MEDIUM, false, false, true, false);
		ExcelStyler.lineOuter(titleRow.getCell(STRAIGHT_LAST_COL), BorderStyle.MEDIUM, false, false, false, true);
		ExcelStyler.bold(titleRow, 0, STRAIGHT_LAST_COL, true);

		// 1-2 셀 merge
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 0, STRAIGHT_LAST_COL);
		return rowIdx;
	}

	private static int generateStraightHeader(Sheet sheet, int rowIdx) {
		// 1. 헤더 값
		Row header = ExcelRowWriter.writeRow(sheet, rowIdx++, r -> {
			r.createCell(0).setCellValue("종류");
			r.createCell(1).setCellValue("종류");
			r.createCell(2).setCellValue("타입");
			r.createCell(3).setCellValue("가공");
			r.createCell(4).setCellValue("NUMBER");
			r.createCell(5).setCellValue("수량");
			r.createCell(6).setCellValue("LITZ WIRE 1");
			r.createCell(7).setCellValue("LITZ WIRE 2");
			r.createCell(8).setCellValue("LITZ WIRE 3");
			r.createCell(9).setCellValue("LITZ WIRE 4");
			r.createCell(10).setCellValue("LITZ WIRE 5");
			r.createCell(11).setCellValue("LITZ WIRE 6");
		});

		// 1-1. 헤더 스타일
		ExcelStyler.fontSize(header, ExcelStyler.H5);
		ExcelRowWriter.setRowHeightPx(header, ExcelStyler.H5_HEIGHT_PX);
		ExcelStyler.backgroundColor(header, 0, STRAIGHT_LAST_COL, ExcelPalette.HEADER_YELLOW);
		ExcelStyler.align(header, 0, STRAIGHT_LAST_COL, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
		ExcelStyler.lineOuter(header, 0, STRAIGHT_LAST_COL, BorderStyle.THIN, true, true, true, true);
		ExcelStyler.lineOuter(header, 0, STRAIGHT_LAST_COL, BorderStyle.MEDIUM, true, false, false, false);
		ExcelStyler.lineOuter(header.getCell(0), BorderStyle.MEDIUM, false, false, true, false);
		ExcelStyler.lineOuter(header.getCell(STRAIGHT_LAST_COL), BorderStyle.MEDIUM, false, false, false, true);
		ExcelStyler.bold(header, 0, STRAIGHT_LAST_COL, true);
		return rowIdx;
	}

	private int generateBranchHeader(Sheet sheet, int rowIdx) {
		// 1. 1차 헤더 값
		Row header1 = ExcelRowWriter.writeRow(sheet, rowIdx++, r -> {
			r.createCell(0).setCellValue("분기 요약");
			r.createCell(3).setCellValue("분기 조립 자재");
		});

		// 1-1 헤더 스타일
		ExcelStyler.backgroundColor(header1, 0, BRANCH_LAST_COL, ExcelPalette.HEADER_GREEN);
		ExcelStyler.align(header1, 0, BRANCH_LAST_COL, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
		ExcelStyler.lineOuter(header1, 0, BRANCH_LAST_COL, BorderStyle.THIN, true, true, true, true);
		ExcelStyler.lineOuter(header1.getCell(0), BorderStyle.MEDIUM, false, false, true, false);
		ExcelStyler.lineOuter(header1.getCell(BRANCH_LAST_COL), BorderStyle.MEDIUM, false, false, false, true);
		ExcelStyler.bold(header1, 0, BRANCH_LAST_COL, true);
		ExcelStyler.fontSize(header1, ExcelStyler.H2);
		ExcelRowWriter.setRowHeightPx(header1, ExcelStyler.H2_HEIGHT_PX);

		// 1-2 셀 합치기
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 0, 2);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 3, 24);

		// 2. 2차 헤더
		Row header2 = ExcelRowWriter.writeRow(sheet, rowIdx++, r -> {
			r.createCell(0).setCellValue("분기 No");
			r.createCell(1).setCellValue("분기 요약");
			r.createCell(2).setCellValue("수량");
			r.createCell(3).setCellValue("상판 조립 Upper Base");
			r.createCell(11).setCellValue("하부 Base");
			r.createCell(18).setCellValue("TRACK CABLE SPT");
		});

		// 2-1 헤더 스타일
		ExcelStyler.backgroundColor(header2, 0, BRANCH_LAST_COL, ExcelPalette.HEADER_YELLOW);
		ExcelStyler.align(header2, 0, BRANCH_LAST_COL, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
		ExcelStyler.lineOuter(header2, 0, BRANCH_LAST_COL, BorderStyle.THIN, true, true, true, true);
		ExcelStyler.lineOuter(header2.getCell(0), BorderStyle.MEDIUM, false, false, true, false);
		ExcelStyler.lineOuter(header2.getCell(BRANCH_LAST_COL), BorderStyle.MEDIUM, false, false, false, true);
		ExcelStyler.bold(header2, 0, BRANCH_LAST_COL, true);
		ExcelStyler.fontSize(header2, ExcelStyler.H3);
		ExcelRowWriter.setRowHeightPx(header2, ExcelStyler.H3_HEIGHT_PX);

		// 1-2 셀 합치기
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 3, 10);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 11, 17);
		ExcelMerges.mergeCols(sheet, rowIdx - 1, 18, 24);

		return rowIdx;
	}

	private static double toDouble(Object v) {
		if (v == null)
			return 0d;
		if (v instanceof Number n)
			return n.doubleValue();
		try {
			return Double.parseDouble(String.valueOf(v));
		} catch (Exception e) {
			return 0d;
		}
	}

	private void insertImage(Sheet sheet, int startRow, int colIdx, String imageUrl) {
		byte[] imageBytes = imageDownloader.downloadImage(imageUrl);
		if (imageBytes == null)
			throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);

		// 가로는 해당 열 전체(30자)로 꽉 채움
		float colWpx = sheet.getColumnWidthInPixels(colIdx);
		int sidePx = Math.round(colWpx);
		int emuH = Units.pixelToEMU(sidePx);

		Workbook wb = sheet.getWorkbook();
		int pictureIdx = wb.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);

		CreationHelper helper = wb.getCreationHelper();
		Drawing<?> drawing = sheet.createDrawingPatriarch();

		ClientAnchor anchor = helper.createClientAnchor();
		anchor.setCol1(colIdx);
		anchor.setRow1(startRow);
		anchor.setCol2(colIdx + 1);
		anchor.setDx1(0);
		anchor.setDx2(0);
		anchor.setRow2(startRow + 5);
		anchor.setDy1(0);
		anchor.setDy2(emuH);

		anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);

		drawing.createPicture(anchor, pictureIdx);
	}
}
