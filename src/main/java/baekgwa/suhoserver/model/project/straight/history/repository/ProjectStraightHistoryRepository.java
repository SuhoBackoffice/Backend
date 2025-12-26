package baekgwa.suhoserver.model.project.straight.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.project.straight.history.entity.ProjectStraightHistoryEntity;

/**
 * PackageName : baekgwa.suhoserver.model.project.straight.history.repository
 * FileName    : ProjectStraightHistoryRepository
 * Author      : Baekgwa
 * Date        : 25. 12. 26.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 26.     Baekgwa               Initial creation
 */
public interface ProjectStraightHistoryRepository extends JpaRepository<ProjectStraightHistoryEntity, Long> {
}
