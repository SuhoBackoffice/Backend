package baekgwa.suhoserver.model.project.branch.branch.repository;

import java.util.List;

import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;

/**
 * PackageName : baekgwa.suhoserver.model.project.branch.branch.repository
 * FileName    : ProjectBranchRepositoryCustom
 * Author      : Baekgwa
 * Date        : 2026-02-24
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-02-24     Baekgwa               Initial creation
 */
public interface ProjectBranchRepositoryCustom {

	List<ProjectBranchEntity> findByProjectWithKeyword(ProjectEntity project, String keyword);
}
