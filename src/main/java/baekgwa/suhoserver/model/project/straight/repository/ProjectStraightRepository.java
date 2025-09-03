package baekgwa.suhoserver.model.project.straight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.entity.ProjectStraightEntity;

/**
 * PackageName : baekgwa.suhoserver.model.project.straight.repository
 * FileName    : ProjectStraightRepository
 * Author      : Baekgwa
 * Date        : 2025-08-09
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-09     Baekgwa               Initial creation
 */
public interface ProjectStraightRepository extends JpaRepository<ProjectStraightEntity, Long> {

	List<ProjectStraightEntity> findByProject(ProjectEntity findProject);

	@Query("SELECT ps FROM ProjectStraightEntity ps JOIN FETCH ps.straightType st WHERE ps.project = :project AND ps.isLoopRail = :isLoopRail ORDER BY ps.length DESC , st.type ASC")
	List<ProjectStraightEntity> findSortedWithType(@Param("project") ProjectEntity project, @Param("isLoopRail") Boolean isLoopRail);
}
