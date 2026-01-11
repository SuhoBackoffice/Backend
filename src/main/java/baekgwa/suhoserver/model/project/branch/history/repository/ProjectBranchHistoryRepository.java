package baekgwa.suhoserver.model.project.branch.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.project.branch.history.entity.ProjectBranchHistoryEntity;

/**
 * PackageName : baekgwa.suhoserver.model.project.branch.history.repository
 * FileName    : ProjectBranchHistoryRepository
 * Author      : Baekgwa
 * Date        : 25. 12. 27.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 27.     Baekgwa               Initial creation
 */
public interface ProjectBranchHistoryRepository extends JpaRepository<ProjectBranchHistoryEntity, Long> {
}
