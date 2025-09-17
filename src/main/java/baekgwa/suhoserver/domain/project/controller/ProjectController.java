package baekgwa.suhoserver.domain.project.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.project.dto.ProjectResponse;
import baekgwa.suhoserver.domain.project.facade.ProjectFacade;
import baekgwa.suhoserver.domain.project.type.ProjectSort;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.PageResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.controller
 * FileName    : ProjectController
 * Author      : Baekgwa
 * Date        : 2025-08-07
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-07     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Tag(name = "Project Controller", description = "프로젝트 컨트롤러")
public class ProjectController {

	private final ProjectFacade projectFacade;

	@PostMapping("/new")
	@Operation(summary = "신규 프로젝트 등록")
	public BaseResponse<ProjectResponse.NewProjectDto> createNewProject(
		@RequestBody @Valid ProjectRequest.PostNewProjectDto postNewProjectDto
	) {
		ProjectResponse.NewProjectDto newProjectDto = projectFacade.createNewProject(postNewProjectDto);
		return BaseResponse.success(SuccessCode.CREATE_NEW_PROJECT_SUCCESS, newProjectDto);
	}

	@PostMapping("/{projectId}/branch")
	@Operation(summary = "프로젝트 분기 정보 등록")
	public BaseResponse<ProjectResponse.NewProjectDto> registerProjectBranch(
		@RequestBody @Valid List<ProjectRequest.PostProjectBranchInfo> postProjectBranchInfoList,
		@PathVariable("projectId") Long projectId
	) {
		ProjectResponse.NewProjectDto newProjectDto =
			projectFacade.registerProjectBranch(postProjectBranchInfoList, projectId);

		return BaseResponse.success(SuccessCode.REGISTER_PROJECT_BRANCH_SUCCESS, newProjectDto);
	}

	@GetMapping("/{projectId}")
	@Operation(summary = "프로젝트 정보 조회")
	public BaseResponse<ProjectResponse.ProjectDetailInfo> getProjectInfo(
		@PathVariable("projectId") Long projectId
	) {
		ProjectResponse.ProjectDetailInfo projectDetailInfo = projectFacade.getProjectInfo(projectId);
		return BaseResponse.success(SuccessCode.GET_PROJECT_DETAIL_INFORMATION_SUCCESS, projectDetailInfo);
	}

	@PostMapping("/{projectId}/straight")
	@Operation(summary = "프로젝트 직선 레일 정보 등록")
	public BaseResponse<Void> registerProjectStraight(
		@RequestBody @Valid List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList,
		@PathVariable("projectId") Long projectId
	) {
		projectFacade.registerProjectStraight(postProjectStraightInfoList, projectId);

		return BaseResponse.success(SuccessCode.REGISTER_PROJECT_NORMAL_STRAIGHT_SUCCESS);
	}

	@GetMapping("/{projectId}/branch")
	@Operation(summary = "프로젝트 분기레일 정보 조회")
	public BaseResponse<List<ProjectResponse.ProjectBranchInfo>> getProjectBranchInfo(
		@PathVariable("projectId") Long projectId
	) {
		List<ProjectResponse.ProjectBranchInfo> projectBranchInfoList = projectFacade.getProjectBranchInfo(projectId);
		return BaseResponse.success(SuccessCode.GET_PROJECT_DETAIL_BRANCH_INFO_SUCCESS, projectBranchInfoList);
	}

	@GetMapping("/{projectId}/straight")
	@Operation(summary = "프로젝트 직선레일 정보 조회")
	public BaseResponse<List<ProjectResponse.ProjectStraightInfo>> getProjectStraightInfo(
		@PathVariable("projectId") Long projectId
	) {
		List<ProjectResponse.ProjectStraightInfo> projectStraightInfoList =
			projectFacade.getProjectStraightInfo(projectId);
		return BaseResponse.success(SuccessCode.GET_PROJECT_DETAIL_STRAIGHT_INFO_SUCCESS, projectStraightInfoList);
	}

	@GetMapping
	@Operation(summary = "프로젝트 리스트 조회")
	public BaseResponse<PageResponse<ProjectResponse.ProjectInfo>> getProjectList(
		@RequestParam(value = "keyword", required = false) String keyword,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "versionId", required = false) Long versionId,
		@RequestParam(value = "startDate", required = false) LocalDate startDate,
		@RequestParam(value = "endDate", required = false) LocalDate endDate,
		@RequestParam(value = "sort", required = false, defaultValue = "START_DATE") ProjectSort sort
	) {
		// dto 객체 생성
		ProjectRequest.GetProjectInfo dto =
			new ProjectRequest.GetProjectInfo(keyword, page, size, versionId, startDate, endDate, sort);

		PageResponse<ProjectResponse.ProjectInfo> projectInfoList = projectFacade.getProjectInfoList(dto);

		return BaseResponse.success(SuccessCode.GET_PROJECT_INFORMATION_SUCCESS, projectInfoList);
	}

	@GetMapping("/sort-type")
	@Operation(summary = "프로젝트 목록 조회의 정렬 조건 목록 조회")
	public BaseResponse<List<ProjectResponse.ProjectSearchSort>> getProjectSearchSort() {
		List<ProjectResponse.ProjectSearchSort> result = Arrays.stream(ProjectSort.values())
			.map(ProjectResponse.ProjectSearchSort::of)
			.toList();

		return BaseResponse.success(SuccessCode.GET_PROJECT_SEARCH_SORT_SUCCESS, result);
	}

	@DeleteMapping("/straight/{projectStraightId}")
	@Operation(summary = "프로젝트의 직선레일 삭제")
	public BaseResponse<Void> deleteProjectStraight(
		@PathVariable("projectStraightId") Long projectStraightId
	) {
		projectFacade.deleteProjectStraight(projectStraightId);
		return BaseResponse.success(SuccessCode.DELETE_PROJECT_STRAIGHT_SUCCESS);
	}

	@PatchMapping("/straight/{projectStraightId}")
	@Operation(summary = "프로젝트의 직선레일 정보 수정")
	public BaseResponse<Void> patchProjectStraight(
		@PathVariable("projectStraightId") Long projectStraightId,
		@RequestBody @Valid ProjectRequest.PatchProjectStraightDto patchProjectStraightDto
	) {
		projectFacade.patchProjectStraight(projectStraightId, patchProjectStraightDto);
		return BaseResponse.success(SuccessCode.PATCH_PROJECT_STRAIGHT_SUCCESS);
	}

	@DeleteMapping("/branch/{projectBranchId}")
	@Operation(summary = "프로젝트 분기레일 삭제")
	public BaseResponse<Void> deleteProjectBranch(
		@PathVariable("projectBranchId") Long projectBranchId
	) {
		projectFacade.deleteProjectBranch(projectBranchId);
		return BaseResponse.success(SuccessCode.DELETE_PROJECT_STRAIGHT_SUCCESS);
	}

	@PatchMapping("/branch/{projectBranchId}")
	@Operation(summary = "프로젝트의 특정 분기레일 정보 수정")
	public BaseResponse<Void> patchProjectBranch(
		@PathVariable("projectBranchId") Long projectBranchId,
		@RequestBody @Valid ProjectRequest.PatchProjectBranchDto patchProjectBranchDto
	) {
		projectFacade.patchProjectBranch(projectBranchId, patchProjectBranchDto);
		return BaseResponse.success(SuccessCode.PATCH_PROJECT_BRANCH_SUCCESS);
	}

	@GetMapping("/{projectId}/quantity-list")
	@Operation(summary = "프로젝트 물량리스트 산출 및 excel 파일 다운로드")
	public ResponseEntity<byte[]> getProjectQuantityList(
		@PathVariable("projectId") Long projectId
	) {
		// 1. 엑셀 데이터 생성 및 응답
		ProjectResponse.ProjectQuantityList response = projectFacade.getProjectQuantityList(projectId);

		// 2. Header 구성
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"");
		headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		// 3. 응답
		return ResponseEntity.ok()
			.headers(headers)
			.body(response.getExcelBytes());
	}
}
