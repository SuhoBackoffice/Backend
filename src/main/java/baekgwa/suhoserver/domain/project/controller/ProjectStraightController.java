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
import baekgwa.suhoserver.domain.project.type.ProjectStraightAnalyzeSort;
import baekgwa.suhoserver.domain.project.type.ProjectStraightCapacitySort;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.controller
 * FileName    : ProjectStraightController
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
@Tag(name = "Project Straight Controller", description = "프로젝트(직선 레일) 컨트롤러")
public class ProjectStraightController {

	private final ProjectFacade projectFacade;

	@PostMapping("/{projectId}/straight")
	@Operation(summary = "프로젝트 직선 레일 정보 등록")
	public BaseResponse<Void> registerProjectStraight(
		@RequestBody @Valid List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList,
		@PathVariable("projectId") Long projectId,
		@AuthenticationPrincipal Long userId
	) {
		projectFacade.registerProjectStraight(postProjectStraightInfoList, projectId, userId);

		return BaseResponse.success(SuccessCode.REGISTER_PROJECT_NORMAL_STRAIGHT_SUCCESS);
	}

	@GetMapping("/{projectId}/straight")
	@Operation(summary = "프로젝트 직선레일 정보 조회")
	public BaseResponse<ProjectResponse.ProjectStraightInfo> getProjectStraightInfo(
		@PathVariable("projectId") Long projectId,
		@RequestParam(value = "length", required = false) String length
	) {
		ProjectResponse.ProjectStraightInfo response = projectFacade.getProjectStraightInfo(projectId, length);
		return BaseResponse.success(SuccessCode.GET_PROJECT_DETAIL_STRAIGHT_INFO_SUCCESS, response);
	}

	@GetMapping("/{projectId}/straight/{projectStraightId}")
	@Operation(summary = "프로젝트 직선레일 상세 정보 조회")
	public BaseResponse<ProjectResponse.ProjectStraightDetailInfo> getProjectStraightDetailInfo(
		@PathVariable("projectId") Long projectId, //Not Use, REST-API Rules
		@PathVariable("projectStraightId") Long projectStraightId
	) {
		ProjectResponse.ProjectStraightDetailInfo response = projectFacade.getProjectStraightDetailInfo(projectStraightId);
		return BaseResponse.success(SuccessCode.GET_PROJECT_DETAIL_STRAIGHT_DETAIL_INFO_SUCCESS, response);
	}

	@DeleteMapping("/straight/{projectStraightId}")
	@Operation(summary = "프로젝트의 직선레일 삭제")
	public BaseResponse<Void> deleteProjectStraight(
		@PathVariable("projectStraightId") Long projectStraightId,
		@AuthenticationPrincipal Long userId
	) {
		projectFacade.deleteProjectStraight(projectStraightId, userId);
		return BaseResponse.success(SuccessCode.DELETE_PROJECT_STRAIGHT_SUCCESS);
	}

	@PatchMapping("/straight/{projectStraightId}")
	@Operation(summary = "프로젝트의 직선레일 정보 수정")
	public BaseResponse<Void> patchProjectStraight(
		@PathVariable("projectStraightId") Long projectStraightId,
		@RequestBody @Valid ProjectRequest.PatchProjectStraightDto patchProjectStraightDto,
		@AuthenticationPrincipal Long userId
	) {
		projectFacade.patchProjectStraight(projectStraightId, patchProjectStraightDto, userId);
		return BaseResponse.success(SuccessCode.PATCH_PROJECT_STRAIGHT_SUCCESS);
	}

	@GetMapping("/straight/capacity/types")
	@Operation(summary = "직선레일 Capacity 정렬 조건 목록 조회")
	public BaseResponse<List<ProjectResponse.BranchCapacitySortType>> getStraightCapacityTypes() {
		List<ProjectResponse.BranchCapacitySortType> response = projectFacade.getStraightCapacityTypes();
		return BaseResponse.success(SuccessCode.GET_STRAIGHT_CAPACITY_SORT_TYPE_SUCCESS, response);
	}

	@GetMapping("/{projectId}/straight/capacity")
	@Operation(summary = "직선레일별 생산 가능 수량(Capacity) 조회")
	public BaseResponse<List<ProjectResponse.ProjectStraightCapacity>> getProjectStraightCapacity(
		@PathVariable("projectId") Long projectId,
		@RequestParam(defaultValue = "CAPACITY") ProjectStraightCapacitySort sort,
		@RequestParam(defaultValue = "ASC") Sort.Direction dir
	) {
		List<ProjectResponse.ProjectStraightCapacity> response =
			projectFacade.getProjectStraightCapacity(projectId, sort, dir);
		return BaseResponse.success(SuccessCode.GET_PROJECT_STRAIGHT_CAPACITY, response);
	}

	@GetMapping("/straight/capacity/analyze/types")
	@Operation(summary = "직선레일 Capacity 분석 정렬 조건 목록 조회")
	public BaseResponse<List<ProjectResponse.BranchCapacitySortType>> getStraightCapacityAnalyzeTypes() {
		List<ProjectResponse.BranchCapacitySortType> response = projectFacade.getStraightCapacityAnalyzeTypes();
		return BaseResponse.success(SuccessCode.GET_STRAIGHT_ANALYZE_SORT_TYPE_SUCCESS, response);
	}

	@GetMapping("/{projectId}/straight/{projectStraightId}/capacity")
	@Operation(summary = "특정 직선레일 자재 부족 분석(Capacity Analyze) 조회")
	public BaseResponse<ProjectResponse.ProjectStraightCapacityAnalyze> getProjectStraightCapacityAnalyze(
		@PathVariable("projectId") Long projectId, //Not Use, REST-API Rules
		@PathVariable("projectStraightId") Long projectStraightId,
		@RequestParam(defaultValue = "SHORTAGE_QUANTITY") ProjectStraightAnalyzeSort sort,
		@RequestParam(defaultValue = "DESC") Sort.Direction dir,
		@RequestParam(defaultValue = "false") boolean onlyShortage
	) {
		ProjectResponse.ProjectStraightCapacityAnalyze response =
			projectFacade.getProjectStraightCapacityAnalyze(projectStraightId, sort, dir, onlyShortage);
		return BaseResponse.success(SuccessCode.GET_PROJECT_STRAIGHT_CAPACITY_ANALYZE, response);
	}
}
