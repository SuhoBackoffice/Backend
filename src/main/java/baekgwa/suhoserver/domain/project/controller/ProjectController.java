package baekgwa.suhoserver.domain.project.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.project.dto.ProjectResponse;
import baekgwa.suhoserver.domain.project.service.ProjectService;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
		@RequestBody ProjectRequest.PostNewProjectDto postNewProjectDto
	) {
		ProjectResponse.NewProjectDto newProjectDto = projectService.createNewProject(postNewProjectDto);
		return BaseResponse.success(SuccessCode.CREATE_NEW_PROJECT_SUCCESS, newProjectDto);
	}

}
