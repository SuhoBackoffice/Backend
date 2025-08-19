package baekgwa.suhoserver.domain.project.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.project.dto.ProjectResponse;
import baekgwa.suhoserver.domain.project.service.ProjectService;
import baekgwa.suhoserver.domain.project.type.RailKind;
import baekgwa.suhoserver.global.response.BaseResponse;
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

	private final ProjectService projectService;

	@PostMapping("/new")
	@Operation(summary = "신규 프로젝트 등록")
	public BaseResponse<ProjectResponse.NewProjectDto> createNewProject (
		@RequestBody @Valid ProjectRequest.PostNewProjectDto postNewProjectDto
	) {
		ProjectResponse.NewProjectDto newProjectDto = projectService.createNewProject(postNewProjectDto);
		return BaseResponse.success(SuccessCode.CREATE_NEW_PROJECT_SUCCESS, newProjectDto);
	}

	@PostMapping("/{projectId}/branch")
	@Operation(summary = "프로젝트 분기 정보 등록")
	public BaseResponse<ProjectResponse.NewProjectDto> registerProjectBranch (
		@RequestBody @Valid List<ProjectRequest.PostProjectBranchInfo> postProjectBranchInfoList,
		@PathVariable("projectId") Long projectId
	) {
		ProjectResponse.NewProjectDto newProjectDto =
			projectService.registerProjectBranch(postProjectBranchInfoList, projectId);

		return BaseResponse.success(SuccessCode.REGISTER_PROJECT_BRANCH_SUCCESS, newProjectDto);
	}

	@PostMapping("/{projectId}/normal-straight")
	@Operation(summary = "프로젝트 직선 레일 정보 등록")
	public BaseResponse<Void> registerProjectStraight(
		@RequestBody @Valid List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList,
		@PathVariable("projectId") Long projectId
	) {
		projectService.registerProjectStraight(postProjectStraightInfoList, projectId, RailKind.NORMAL);

		return BaseResponse.success(SuccessCode.REGISTER_PROJECT_NORMAL_STRAIGHT_SUCCESS);
	}

	@PostMapping("/{projectId}/loop-straight")
	@Operation(summary = "프로젝트 루프 레일 정보 등록")
	public BaseResponse<Void> registerProjectLoopStraight(
		@RequestBody @Valid List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList,
		@PathVariable("projectId") Long projectId
	) {
		projectService.registerProjectStraight(postProjectStraightInfoList, projectId, RailKind.LOOP);

		return BaseResponse.success(SuccessCode.REGISTER_PROJECT_NORMAL_STRAIGHT_SUCCESS);
	}

	@GetMapping("/{projectId}")
	@Operation(summary = "프로젝트 정보 조회")
	public BaseResponse<ProjectResponse.ProjectDetailInfo> getProjectInfo (
		@PathVariable("projectId") Long projectId
	) {
		ProjectResponse.ProjectDetailInfo projectDetailInfo = projectService.getProjectInfo(projectId);
		return BaseResponse.success(SuccessCode.GET_PROJECT_INFORMATION_SUCCESS, projectDetailInfo);
	}
}
