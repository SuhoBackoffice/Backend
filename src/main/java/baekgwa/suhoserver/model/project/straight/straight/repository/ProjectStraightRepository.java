package baekgwa.suhoserver.model.project.straight.straight.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;

/**
 * PackageName : baekgwa.suhoserver.model.project.straight.straight.repository
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

	List<ProjectStraightEntity> findByProjectOrderByLength(ProjectEntity project);

	List<ProjectStraightEntity> findByProject(ProjectEntity findProject);

	@Query("SELECT ps FROM ProjectStraightEntity ps JOIN FETCH ps.straightType st WHERE ps.project = :project AND ps.isLoopRail = :isLoopRail ORDER BY ps.length DESC , st.type ASC")
	List<ProjectStraightEntity> findSortedWithType(@Param("project") ProjectEntity project,
		@Param("isLoopRail") Boolean isLoopRail);

	@Query("SELECT ps "
		+ "FROM ProjectStraightEntity ps "
		+ "JOIN FETCH ps.straightType "
		+ "WHERE ps.project = :project "
		+ "AND ps.completedQuantity < ps.totalQuantity")
	List<ProjectStraightEntity> findUnCompletedByProject(@Param("project") ProjectEntity findProject);

	@Query("SELECT ps FROM ProjectStraightEntity ps WHERE ps.project = :project  AND str(ps.length) LIKE concat('%', :length, '%')")
	List<ProjectStraightEntity> findByProjectAndLengthLikeOrderByLength(
		@Param("project") ProjectEntity project,
		@Param("length") String length
	);

	@Query("SELECT ps FROM ProjectStraightEntity ps JOIN FETCH ps.straightType st WHERE ps.project = :project ORDER BY ps.length ASC, st.type ASC")
	List<ProjectStraightEntity> findByProjectWithStraightType(@Param("project") ProjectEntity project);

	@Query("SELECT ps FROM ProjectStraightEntity ps JOIN FETCH ps.straightType WHERE ps.id = :id")
	Optional<ProjectStraightEntity> findByIdWithStraightType(@Param("id") Long id);
}
