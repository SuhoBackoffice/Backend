package baekgwa.suhoserver.domain.project.service;

import static java.lang.Boolean.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.project.dto.ProjectResponse;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.global.response.PageResponse;
import baekgwa.suhoserver.infra.download.ImageDownloader;
import baekgwa.suhoserver.infra.excel.util.ExcelMerges;
import baekgwa.suhoserver.infra.excel.util.ExcelPalette;
import baekgwa.suhoserver.infra.excel.util.ExcelRowWriter;
import baekgwa.suhoserver.infra.excel.util.ExcelStyler;
import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.branch.bom.repository.BranchBomRepository;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import baekgwa.suhoserver.model.branch.type.repository.BranchTypeRepository;
import baekgwa.suhoserver.model.project.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.repository.ProjectBranchRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.project.repository.ProjectRepository;
import baekgwa.suhoserver.model.project.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.project.straight.repository.ProjectStraightRepository;
import baekgwa.suhoserver.model.straight.type.entity.StraightTypeEntity;
import baekgwa.suhoserver.model.straight.type.repository.StraightTypeRepository;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import baekgwa.suhoserver.model.version.repository.VersionInfoRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.domain.project.service
 * FileName    : ProjectService
 * Author      : Baekgwa
 * Date        : 2025-08-07
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-07     Baekgwa               Initial creation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final ProjectBranchRepository projectBranchRepository;
	private final BranchTypeRepository branchTypeRepository;
	private final StraightTypeRepository straightTypeRepository;
	private final VersionInfoRepository versionInfoRepository;
	private final ProjectStraightRepository projectStraightRepository;
	private final BranchBomRepository branchBomRepository;

	private final ImageDownloader imageDownloader;

	private static final BigDecimal LITZ_WIRE_MAX = BigDecimal.valueOf(1800L);
	private static final BigDecimal OFFSET_215 = BigDecimal.valueOf(215L);
	private static final BigDecimal TH_1200 = BigDecimal.valueOf(1200L);
	private static final BigDecimal TH_2400 = BigDecimal.valueOf(2400L);
	private static final BigDecimal TH_3600 = BigDecimal.valueOf(3600L);

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

	@Transactional
	public ProjectResponse.NewProjectDto createNewProject(ProjectRequest.PostNewProjectDto postNewProjectDto) {
		// 1. 입력값 유효성 검증
		if (postNewProjectDto.getStartDate() != null &&
			postNewProjectDto.getEndDate() != null &&
			!postNewProjectDto.getEndDate().isAfter(postNewProjectDto.getStartDate())) {
			throw new GlobalException(ErrorCode.PROJECT_END_AFTER_START_ERROR);
		}

		// 2. version info entity 조회 및 유효성 검증
		VersionInfoEntity findVersion = versionInfoRepository.findById(postNewProjectDto.getVersionId())
			.orElseThrow(
				() -> new GlobalException(ErrorCode.NOT_FOUND_VERSION));

		// 3. Entity 생성 및 저장
		ProjectEntity newProject = ProjectEntity.createNewProject(findVersion, postNewProjectDto.getName(),
			postNewProjectDto.getRegion(), postNewProjectDto.getStartDate(), postNewProjectDto.getEndDate());
		ProjectEntity savedProject = projectRepository.save(newProject);

		return new ProjectResponse.NewProjectDto(savedProject.getId());
	}

	@Transactional
	public ProjectResponse.NewProjectDto registerProjectBranch(
		List<ProjectRequest.PostProjectBranchInfo> postProjectBranchInfoList, Long projectId
	) {
		// 1. 프로젝트 조회
		ProjectEntity findProject = projectRepository.findById(projectId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. branch 정보 조회
		List<Long> branchIdList = postProjectBranchInfoList.stream()
			.map(ProjectRequest.PostProjectBranchInfo::getBranchTypeId)
			.distinct()
			.toList();
		List<BranchTypeEntity> findBranchTypeList = branchTypeRepository.findAllById(branchIdList);

		// 2-1. 현재 프로젝트의 모든 분기 레일 정보를 불러와서, 이미 있는 분기 레일을 또 추가하려는 건 아닌지 검증.
		List<Long> findBranchTypeIdList = findBranchTypeList.stream().map(BranchTypeEntity::getId).toList();
		List<String> existBranchCode = projectBranchRepository.findAllByBranchTypeIdIn(findBranchTypeIdList)
			.stream().map(data -> data.getBranchType().getCode()).toList();

		// 2-2. 추가 검증. 찾아온 branchType 이 id List 보다 작으면, 잘못된 id가 입력된 경우. (없는 것)
		if (branchIdList.size() > findBranchTypeList.size())
			throw new GlobalException(ErrorCode.NOT_FOUND_BRANCH_TYPE);

		// 2-3. branch 정보 stream 으로 map 처리
		Map<Long, BranchTypeEntity> findBranchTypeMap = findBranchTypeList.stream()
			.collect(Collectors.toMap(BranchTypeEntity::getId, Function.identity()));

		// 3. ProjectBranch Entity List 생성
		List<ProjectBranchEntity> newProjectBranchList = postProjectBranchInfoList.stream()
			.map(dto -> {
				BranchTypeEntity branchType = findBranchTypeMap.get(dto.getBranchTypeId());
				if (branchType == null) {
					throw new GlobalException(ErrorCode.NOT_FOUND_BRANCH_TYPE);
				}
				if (!findProject.getVersionInfoEntity().getId().equals(branchType.getVersionInfoEntity().getId())) {
					throw new GlobalException(ErrorCode.INVALID_VERSION_BRANCH);
				}
				if (existBranchCode.contains(branchType.getCode())) {
					throw new GlobalException(ErrorCode.ALREADY_EXIST_PROJECT_BRANCH_DATA);
				}
				return ProjectBranchEntity.createNewProjectBranch(findProject, branchType, dto.getQuantity());
			}).toList();

		// 4. 전체 저장
		projectBranchRepository.saveAll(newProjectBranchList);

		return new ProjectResponse.NewProjectDto(projectId);
	}

	@Transactional(readOnly = true)
	public ProjectResponse.ProjectDetailInfo getProjectInfo(Long projectId) {
		// 1. Project 유효성 검증 및 Data 조회
		ProjectEntity findProject = projectRepository.findById(projectId).orElseThrow(
			() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. dto 변환 후 반환
		return ProjectResponse.ProjectDetailInfo.of(findProject);
	}

	@Transactional(readOnly = true)
	public List<ProjectResponse.ProjectBranchInfo> getProjectBranchInfo(Long projectId) {
		// 1. Project 유효성 검증 및 Data 조회
		ProjectEntity findProject = projectRepository.findById(projectId).orElseThrow(
			() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. Project 의 Branch 정보 조회 및 응답
		List<ProjectBranchEntity> findProjectBranchList = projectBranchRepository.findByProject(findProject);
		return findProjectBranchList.stream()
			.map(ProjectResponse.ProjectBranchInfo::of)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<ProjectResponse.ProjectStraightInfo> getProjectStraightInfo(Long projectId) {
		// 1. Project 유효성 검증 및 Data 조회
		ProjectEntity findProject = projectRepository.findById(projectId).orElseThrow(
			() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. Project 의 Straight 정보 조회
		List<ProjectStraightEntity> findProjectStraightList = projectStraightRepository.findByProject(findProject);
		return findProjectStraightList.stream()
			.map(data -> {
				BigDecimal holePosition = calcHolePosition(data);
				ProjectResponse.LitzInfo litzInfo = generateLitzInfoList(data);
				return ProjectResponse.ProjectStraightInfo.of(data, litzInfo, holePosition);
			})
			.toList();
	}

	@Transactional
	public void registerProjectStraight(
		List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList, Long projectId
	) {
		// 1. 프로젝트 정보 조회
		ProjectEntity findProject = projectRepository.findById(projectId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. 입력 데이터 중복 및 기타 데이터 중복 검증.
		validateDuplicationStraight(postProjectStraightInfoList, findProject);

		// 3. straightTypeId 으로, 필요한 StraightType List 조회
		List<Long> straightTypeIdList = postProjectStraightInfoList.stream()
			.map(ProjectRequest.PostProjectStraightInfo::getStraightTypeId)
			.distinct()
			.toList();
		List<StraightTypeEntity> findStraightTypeList = straightTypeRepository.findAllById(straightTypeIdList);

		// 3-1. id 기반으로, 검색 가능하도록 Map 으로 convert
		Map<Long, StraightTypeEntity> findStraightTypeMap =
			findStraightTypeList.stream().collect(Collectors.toMap(StraightTypeEntity::getId, Function.identity()));

		// 4. ProjectStraightEntity List 생성 및 저장
		List<ProjectStraightEntity> projectStraightList =
			getProjectStraightList(postProjectStraightInfoList, findStraightTypeMap, findProject);

		// 5. 저장 및 반환
		projectStraightRepository.saveAll(projectStraightList);
	}

	@Transactional(readOnly = true)
	public PageResponse<ProjectResponse.ProjectInfo> getProjectInfoList(ProjectRequest.GetProjectInfo dto) {

		// 1. 페이지네이션 파라미터 유효성 검증
		if (dto.getPage() < 0 || dto.getSize() < 1) {
			throw new GlobalException(ErrorCode.INVALID_PAGINATION_PARAMETER);
		}

		// 1-1. StartDate, EndDate 검증
		if (dto.getStartDate() != null &&
			dto.getEndDate() != null &&
			!dto.getEndDate().isAfter(dto.getStartDate())) {
			throw new GlobalException(ErrorCode.PROJECT_END_AFTER_START_ERROR);
		}

		// 2. version 유효성 검증
		if (dto.getVersionId() != null && !versionInfoRepository.existsById(dto.getVersionId())) {
			throw new GlobalException(ErrorCode.NOT_FOUND_VERSION);
		}

		// 3. list 조회
		Page<ProjectResponse.ProjectInfo> findData = projectRepository.searchProjectList(dto);

		return PageResponse.of(findData);
	}

	@Transactional
	public void deleteProjectStraight(Long projectStraightId) {
		if (!projectStraightRepository.existsById(projectStraightId)) {
			throw new GlobalException(ErrorCode.NOT_EXIST_PROJECT_STRAIGHT);
		}
		projectStraightRepository.deleteById(projectStraightId);
	}

	@Transactional
	public void deleteProjectBranch(Long projectBranchId) {
		if (!projectBranchRepository.existsById(projectBranchId)) {
			throw new GlobalException(ErrorCode.NOT_EXIST_PROJECT_BRANCH);
		}
		projectBranchRepository.deleteById(projectBranchId);
	}

	@Transactional
	public void patchProjectStraight(Long projectStraightId, ProjectRequest.PatchProjectStraightDto dto) {
		// 1. projectStraight Entity 조회
		ProjectStraightEntity findProjectStraight = projectStraightRepository.findById(projectStraightId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_PROJECT_STRAIGHT));

		// 2. 업데이트 처리
		findProjectStraight.patchProjectStraight(dto.getTotalQuantity());
	}

	@Transactional
	public void patchProjectBranch(Long projectBranchId, ProjectRequest.PatchProjectBranchDto dto) {
		// 1. projectBranch Entity 조회
		ProjectBranchEntity findProjectBranch = projectBranchRepository.findById(projectBranchId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_PROJECT_BRANCH));

		// 2. 업데이트 처리
		findProjectBranch.patchProjectBranch(dto.getTotalQuantity());
	}

	@Transactional(readOnly = true)
	public ProjectResponse.ProjectQuantityList getProjectQuantityList(Long projectId) {
		// 1. 프로젝트 정보 조회
		ProjectEntity findProject = projectRepository.findById(projectId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. 파일명 생성 및 인코딩 처리
		String fileName = "[" + findProject.getRegion() + "]" + findProject.getName() + "_" + LocalDate.now() + ".xlsx";
		String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "_");

		// 3. WorkSheet 생성
		Workbook workbook = new SXSSFWorkbook(200);

		// 4. 직선레일 물량리스트 제작
		createStraightRailWorkSheet(workbook, findProject);

		// 5. 분기레일 물량리스트 제작
		createBranchRailWorkSheet(workbook, findProject);

		// 6. 설치자재 물량리스트 제작

		// 7. 응답
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
			ProjectResponse.LitzInfo litzInfo = generateLitzInfoList(e);

			Row row = ExcelRowWriter.writeRow(sheet, rowIdx++, r -> {
				r.createCell(STRAIGHT_COL_KIND).setCellValue("LOOP");
				r.createCell(STRAIGHT_COL_LEN).setCellValue(e.getLength());
				r.createCell(STRAIGHT_COL_TYPE).setCellValue(e.getStraightType().getType());
				r.createCell(STRAIGHT_COL_PROC).setCellValue(String.valueOf(calcHolePosition(e)));
				r.createCell(STRAIGHT_COL_NUM)
					.setCellValue("LR-" + e.getLength() + "-" + e.getStraightType().getType());
				r.createCell(STRAIGHT_COL_QTY).setCellValue(e.getTotalQuantity());
				r.createCell(6).setCellValue(toDouble(litzInfo.getLitz1()));
				r.createCell(7).setCellValue(toDouble(litzInfo.getLitz2()));
				r.createCell(8).setCellValue(toDouble(litzInfo.getLitz3()));
				r.createCell(9).setCellValue(toDouble(litzInfo.getLitz4()));
				r.createCell(10).setCellValue(toDouble(litzInfo.getLitz5()));
				r.createCell(11).setCellValue(toDouble(litzInfo.getLitz6()));
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
			ProjectResponse.LitzInfo l = generateLitzInfoList(e);

			Row row = ExcelRowWriter.writeRow(sheet, rowIdx++, r -> {
				r.createCell(STRAIGHT_COL_KIND).setCellValue("일반");
				r.createCell(STRAIGHT_COL_LEN).setCellValue(e.getLength());
				r.createCell(STRAIGHT_COL_TYPE).setCellValue(e.getStraightType().getType());
				r.createCell(STRAIGHT_COL_PROC).setCellValue("");
				r.createCell(STRAIGHT_COL_NUM)
					.setCellValue("SR-" + e.getLength() + "-" + e.getStraightType().getType());
				r.createCell(STRAIGHT_COL_QTY).setCellValue(e.getTotalQuantity());
				r.createCell(6).setCellValue(toDouble(l.getLitz1()));
				r.createCell(7).setCellValue(toDouble(l.getLitz2()));
				r.createCell(8).setCellValue(toDouble(l.getLitz3()));
				r.createCell(9).setCellValue(toDouble(l.getLitz4()));
				r.createCell(10).setCellValue(toDouble(l.getLitz5()));
				r.createCell(11).setCellValue(toDouble(l.getLitz6()));
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

	private @NotNull BigDecimal calcHolePosition(ProjectStraightEntity projectStraight) {
		// 1. 루프레일이 아닌 경우
		if (FALSE.equals(projectStraight.getIsLoopRail())) {
			return BigDecimal.ZERO;
		}

		// 2. 데이터 추출
		String type = projectStraight.getStraightType().getType().substring(0, 1).toUpperCase();
		BigDecimal loopLitzWire = projectStraight.getProject().getVersionInfoEntity().getLoopLitzWire();
		Long length = projectStraight.getLength();

		// 3. 가공 위치 계산
		return switch (type) {
			case "C" -> BigDecimal.valueOf(length)
				.divide(BigDecimal.valueOf(2));
			case "E" -> BigDecimal.valueOf(length)
				.subtract(loopLitzWire); // End: length - loopLitzWire
			case "S" -> BigDecimal.valueOf(length)
				.subtract(BigDecimal.valueOf(215))
				.subtract(loopLitzWire); // ✅ Side: length - 215 - loopLitzWire
			default -> BigDecimal.ZERO;
		};
	}

	private @NotNull ProjectResponse.LitzInfo generateLitzInfoList(ProjectStraightEntity projectStraight) {
		// OffsetType 및 LoopType 추출
		String rawType = projectStraight.getStraightType().getType().toUpperCase();
		boolean loop = TRUE.equals(projectStraight.getIsLoopRail());

		String offsetType;
		String loopType;
		if (loop) {
			if (rawType.length() >= 2) {
				// "CA" -> "A", "EC" -> "C"
				loopType = rawType.substring(0, 1);
				offsetType = rawType.substring(1, 2);
			} else {
				throw new GlobalException(ErrorCode.INVALID_LOOP_RAIL_TYPE_DATA);
			}
		} else {
			loopType = null;
			offsetType = rawType;
		}

		// 1. baseLitzWire 계산
		long length = projectStraight.getLength();
		BigDecimal loopLitzWire = projectStraight.getProject().getVersionInfoEntity().getLoopLitzWire();
		Map<Integer, BigDecimal> baseLitzWireMap = FALSE.equals(projectStraight.getIsLoopRail())
			? baseLitzWireSupporter(length)
			: baseLoopLitzWireSupporter(length, loopType, loopLitzWire);

		// 2. 타입에 따라 offset 되어야 할 번호 확인
		Map<LitzWireAnchor, Integer> anchorMap = pickAnchors(baseLitzWireMap);

		switch (offsetType) {
			case "B" -> dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LU));
			case "C" -> dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LD));
			case "D" -> {
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LU));
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.RU));
			}
			case "E" -> {
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LU));
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.RD));
			}
			case "F" -> {
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LD));
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.RU));
			}
			case "G" -> {
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LU));
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LD));
			}
			case "A" -> { /* no-op */ }
			default -> { /* 변경 없음 */ }
		}

		return ProjectResponse.LitzInfo.from(baseLitzWireMap);
	}

	private Map<LitzWireAnchor, Integer> pickAnchors(Map<Integer, BigDecimal> baseLitzWire) {
		int ru;
		if (present(baseLitzWire, 5)) {
			ru = 5;
		} else if (present(baseLitzWire, 3)) {
			ru = 3;
		} else {
			ru = 1;
		}

		int rd;
		if (present(baseLitzWire, 6)) {
			rd = 6;
		} else if (present(baseLitzWire, 4)) {
			rd = 4;
		} else {
			rd = 2;
		}

		return Map.of(
			LitzWireAnchor.LU, 1,
			LitzWireAnchor.LD, 2,
			LitzWireAnchor.RU, ru,
			LitzWireAnchor.RD, rd
		);
	}

	private boolean present(Map<Integer, BigDecimal> m, int idx) {
		return m.getOrDefault(idx, BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0;
	}

	private Map<Integer, BigDecimal> baseLoopLitzWireSupporter(
		Long length, String loopType, BigDecimal loopLitzWire
	) {
		BigDecimal len = BigDecimal.valueOf(length);
		BigDecimal loop = loopLitzWire; // scale=1 가정
		Map<Integer, BigDecimal> m = new HashMap<>();

		switch (loopType) {
			case "C" -> {
				BigDecimal perSide = len.subtract(loop.multiply(BigDecimal.valueOf(2L)))
					.divide(BigDecimal.valueOf(2L), 1, java.math.RoundingMode.HALF_UP);
				perSide = perSide.max(BigDecimal.ZERO);
				m.put(1, perSide);
				m.put(2, perSide);
				m.put(3, perSide);
				m.put(4, perSide);
			}
			case "E" -> {
				BigDecimal leftBase;
				if (len.compareTo(LITZ_WIRE_MAX) <= 0) {
					leftBase = len;
				} else if (len.compareTo(TH_2400) <= 0) {
					leftBase = TH_1200;
				} else {
					leftBase = LITZ_WIRE_MAX;
				}
				BigDecimal remaining = len.subtract(leftBase).max(BigDecimal.ZERO);
				BigDecimal right = remaining.subtract(loop.multiply(BigDecimal.valueOf(2L))).max(BigDecimal.ZERO);
				m.put(1, leftBase);
				m.put(2, leftBase);
				if (right.signum() > 0) {
					m.put(3, right);
					m.put(4, right);
				}
			}
			case "S" -> {
				BigDecimal leftBase = len.min(LITZ_WIRE_MAX);
				BigDecimal remaining = len.subtract(leftBase);
				BigDecimal deduct = loop.multiply(BigDecimal.valueOf(2L)).add(OFFSET_215);

				if (remaining.signum() > 0) {
					BigDecimal right = remaining.subtract(deduct).max(BigDecimal.ZERO);
					m.put(1, leftBase);
					m.put(2, leftBase);
					m.put(3, right);
					m.put(4, right);
					m.put(5, OFFSET_215);
					m.put(6, OFFSET_215);
				} else {
					BigDecimal left = leftBase.subtract(deduct).max(BigDecimal.ZERO);
					m.put(1, left);
					m.put(2, left);
					m.put(3, OFFSET_215);
					m.put(4, OFFSET_215);
				}
			}
			default -> {
				return baseLitzWireSupporter(length);
			}
		}
		return m;
	}

	private Map<Integer, BigDecimal> baseLitzWireSupporter(long length) {
		BigDecimal len = BigDecimal.valueOf(length);
		Map<Integer, BigDecimal> m = new HashMap<>();

		if (len.compareTo(LITZ_WIRE_MAX) <= 0) {
			m.put(1, len);
			m.put(2, len);
		} else if (len.compareTo(TH_2400) <= 0) {
			BigDecimal rest = len.subtract(TH_1200);
			m.put(1, TH_1200);
			m.put(2, TH_1200);
			m.put(3, rest);
			m.put(4, rest);
		} else { // <= 3600
			BigDecimal rest = len.subtract(LITZ_WIRE_MAX);
			m.put(1, LITZ_WIRE_MAX);
			m.put(2, LITZ_WIRE_MAX);
			m.put(3, rest);
			m.put(4, rest);
		}
		return m;
	}

	// litzWire 차감 진행
	private void dec(Map<Integer, BigDecimal> m, int idx) {
		BigDecimal v = m.getOrDefault(idx, BigDecimal.ZERO);
		v = v.subtract(OFFSET_215).max(BigDecimal.ZERO);
		m.put(idx, v);
	}

	private static List<ProjectStraightEntity> getProjectStraightList(
		List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList,
		Map<Long, StraightTypeEntity> findStraightTypeMap, ProjectEntity findProject) {
		return postProjectStraightInfoList.stream().map(
				dto -> {
					StraightTypeEntity findStraightType = findStraightTypeMap.get(dto.getStraightTypeId());
					if (findStraightType == null) {
						throw new GlobalException(ErrorCode.NOT_FOUND_STRAIGHT_TYPE);
					}
					if (!Objects.equals(findStraightType.getIsLoopRail(), dto.getIsLoopRail())) {
						if (TRUE.equals(dto.getIsLoopRail())) {
							throw new GlobalException(ErrorCode.NOT_MATCH_STRAIGHT_LOOP_TYPE);
						} else {
							throw new GlobalException(ErrorCode.NOT_MATCH_STRAIGHT_NORMAL_TYPE);
						}
					}
					return ProjectStraightEntity
						.createNewStraight(findProject, findStraightType, dto.getTotalQuantity(), dto.getIsLoopRail(),
							dto.getLength());
				})
			.toList();
	}

	private void validateDuplicationStraight(List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList,
		ProjectEntity findProject) {

		// 1-0. 요청 내부 중복 차단용 record
		record StraightKey(Long length, Long straightTypeId) {
		}

		Set<StraightKey> requestKeySet = new HashSet<>();
		for (ProjectRequest.PostProjectStraightInfo dto : postProjectStraightInfoList) {
			StraightKey key = new StraightKey(dto.getLength(), dto.getStraightTypeId());
			if (!requestKeySet.add(key)) {
				throw new GlobalException(ErrorCode.INVALID_PROJECT_STRAIGHT_REGISTER_DATA_DUPLICATION);
			}
		}

		// 1-1. DB 중복 차단: 해당 프로젝트의 기존 (length, straightTypeId)와 교집합 확인
		List<ProjectStraightEntity> existing = projectStraightRepository.findByProject(findProject);
		Set<StraightKey> existingKeySet = existing.stream()
			.map(e -> new StraightKey(e.getLength(), e.getStraightType().getId()))
			.collect(Collectors.toSet());

		for (StraightKey k : requestKeySet) {
			if (existingKeySet.contains(k)) {
				throw new GlobalException(ErrorCode.ALREADY_EXIST_PROJECT_STRAIGHT_DATA);
			}
		}
	}

	private void insertImage(Sheet sheet, int startRow, int colIdx, String imageUrl) {
		byte[] imageBytes = imageDownloader.downloadImage(imageUrl);
		if (imageBytes == null)
			throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);

		// 가로는 해당 열 전체(30자)로 꽉 채움
		float colWpx = sheet.getColumnWidthInPixels(colIdx);
		int sidePx = Math.round(colWpx);            // 정사각형이면 세로도 동일 픽셀
		int emuH = Units.pixelToEMU(sidePx);      // 세로 높이(EMU)

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
