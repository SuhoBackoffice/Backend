package baekgwa.suhoserver.domain.project.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import baekgwa.suhoserver.domain.project.type.ProjectBranchAnalyzeSort;
import baekgwa.suhoserver.domain.project.type.ProjectBranchCapacitySort;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.controller
 * FileName    : ProjectBranchController
 * Author      : Baekgwa
 * Date        : 26. 2. 19.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 19.     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Tag(name = "Project Branch Controller", description = "프로젝트(분기레일) 컨트롤러")
public class ProjectBranchController {

	private final ProjectFacade projectFacade;

	@PostMapping("/{projectId}/branch")
	@Operation(summary = "프로젝트 분기 정보 등록")
	public BaseResponse<ProjectResponse.NewProjectDto> registerProjectBranch(
		@RequestBody @Valid List<ProjectRequest.PostProjectBranchInfo> postProjectBranchInfoList,
		@PathVariable("projectId") Long projectId,
		@AuthenticationPrincipal Long userId
	) {
		ProjectResponse.NewProjectDto newProjectDto =
			projectFacade.registerProjectBranch(postProjectBranchInfoList, projectId, userId);

		return BaseResponse.success(SuccessCode.REGISTER_PROJECT_BRANCH_SUCCESS, newProjectDto);
	}

	@GetMapping("/{projectId}/branch")
	@Operation(summary = "프로젝트 분기레일 정보 조회")
	public BaseResponse<List<ProjectResponse.ProjectBranchInfo>> getProjectBranchInfo(
		@PathVariable("projectId") Long projectId,
		@RequestParam(value = "keyword", required = false) String keyword
	) {
		List<ProjectResponse.ProjectBranchInfo> projectBranchInfoList = projectFacade.getProjectBranchInfo(projectId,
			keyword);
		return BaseResponse.success(SuccessCode.GET_PROJECT_DETAIL_BRANCH_INFO_SUCCESS, projectBranchInfoList);
	}

	@GetMapping("/{projectId}/branch/{projectBranchId}")
	@Operation(summary = "프로젝트 분기 레일 상세 정보 조회")
	public BaseResponse<ProjectResponse.ProjectBranchDetailInfo> getProjectStraightDetailInfo(
		@PathVariable("projectId") Long projectId, //Not Use, REST-API Rules
		@PathVariable("projectBranchId") Long projectBranchId
	) {
		ProjectResponse.ProjectBranchDetailInfo response = projectFacade.getProjectBranchDetailInfo(projectBranchId);
		return BaseResponse.success(SuccessCode.GET_PROJECT_DETAIL_BRANCH_DETAIL_INFO_SUCCESS, response);
	}

	@DeleteMapping("/branch/{projectBranchId}")
	@Operation(summary = "프로젝트 분기레일 삭제")
	public BaseResponse<Void> deleteProjectBranch(
		@PathVariable("projectBranchId") Long projectBranchId,
		@AuthenticationPrincipal Long userId
	) {
		projectFacade.deleteProjectBranch(projectBranchId, userId);
		return BaseResponse.success(SuccessCode.DELETE_PROJECT_STRAIGHT_SUCCESS);
	}

	@PatchMapping("/branch/{projectBranchId}")
	@Operation(summary = "프로젝트의 특정 분기레일 정보 수정")
	public BaseResponse<Void> patchProjectBranch(
		@PathVariable("projectBranchId") Long projectBranchId,
		@RequestBody @Valid ProjectRequest.PatchProjectBranchDto patchProjectBranchDto,
		@AuthenticationPrincipal Long userId
	) {
		projectFacade.patchProjectBranch(projectBranchId, patchProjectBranchDto, userId);
		return BaseResponse.success(SuccessCode.PATCH_PROJECT_BRANCH_SUCCESS);
	}

	@GetMapping("/branch/capacity/types")
	@Operation(summary = "분기 레일 Capacity 정렬 조건")
	public BaseResponse<List<ProjectResponse.BranchCapacitySortType>> getBranchCapacityTypes() {
		return BaseResponse.success(SuccessCode.GET_BRANCH_CAPACITY_SORT_TYPE_SUCCESS,
			projectFacade.getMaterialHistoryTypes());
	}

	@GetMapping("/{projectId}/branch/capacity")
	@Operation(summary = "프로젝트에 할당된 분기레일별 Capacity 조회")
	public BaseResponse<List<ProjectResponse.ProjectBranchCapacity>> getProjectBranchCapacity(
		@PathVariable("projectId") Long projectId,
		@RequestParam(value = "sort", required = false, defaultValue = "CAPACITY") ProjectBranchCapacitySort sort,
		@RequestParam(value = "dir", required = false, defaultValue = "ASC") Sort.Direction dir
	) {
		List<ProjectResponse.ProjectBranchCapacity> projectBranchCapacityList =
			projectFacade.getProjectBranchCapacity(projectId, sort, dir);
		return BaseResponse.success(SuccessCode.GET_PROJECT_BRANCH_CAPACITY, projectBranchCapacityList);
	}

	@GetMapping("/branch/capacity/analyze/types")
	@Operation(summary = "분기 레일 Capacity 분석 정렬 조건")
	public BaseResponse<List<ProjectResponse.BranchCapacitySortType>> getBranchCapacityAnalyzeTypes() {
		return BaseResponse.success(
			SuccessCode.GET_BRANCH_ANALYZE_SORT_TYPE_SUCCESS,
			projectFacade.getAnalyzeTypes()
		);
	}

	@GetMapping("/{projectId}/branch/{projectBranchId}/capacity")
	@Operation(summary = "프로젝트에 할당된 분기레일별 Capacity 분석")
	public BaseResponse<ProjectResponse.ProjectBranchCapacityAnalyze> getProjectBranchCapacityAnalyze(
		@PathVariable("projectId") Long projectId,
		@PathVariable("projectBranchId") Long projectBranchId,
		@RequestParam(value = "sort", required = false, defaultValue = "SHORTAGE_QUANTITY") ProjectBranchAnalyzeSort sort,
		@RequestParam(value = "dir", required = false, defaultValue = "DESC") Sort.Direction dir,
		@RequestParam(value = "onlyShortage", required = false, defaultValue = "false") boolean onlyShortage
	) {
		return BaseResponse.success(
			SuccessCode.GET_PROJECT_BRANCH_CAPACITY_ANALYZE,
			projectFacade.getProjectBranchCapacityAnalyze(projectBranchId, sort, dir, onlyShortage)
		);
	}
}
