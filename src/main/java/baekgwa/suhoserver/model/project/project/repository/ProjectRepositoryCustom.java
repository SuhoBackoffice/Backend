package baekgwa.suhoserver.model.project.project.repository;

import org.springframework.data.domain.Page;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.project.dto.ProjectResponse;

/**
 * PackageName : baekgwa.suhoserver.model.project.project.repository
 * FileName    : ProjectRepositoryCustom
 * Author      : Baekgwa
 * Date        : 2025-08-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-19     Baekgwa               Initial creation
 */
public interface ProjectRepositoryCustom {

	Page<ProjectResponse.ProjectInfo> searchProjectList(ProjectRequest.GetProjectInfo dto);
}
