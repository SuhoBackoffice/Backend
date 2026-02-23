package baekgwa.suhoserver.model.project.straight.bom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.project.straight.bom.entity.ProjectStraightBomEntity;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;

/**
 * PackageName : baekgwa.suhoserver.model.project.straight.bom.repository
 * FileName    : ProjectStraightBomRepository
 * Author      : Baekgwa
 * Date        : 26. 2. 21.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 21.     Baekgwa               Initial creation
 */
public interface ProjectStraightBomRepository extends JpaRepository<ProjectStraightBomEntity, Long> {
	List<ProjectStraightBomEntity> findAllByProjectStraight(ProjectStraightEntity projectStraight);

	@Query("SELECT psb FROM ProjectStraightBomEntity psb WHERE psb.projectStraight.id = :projectStraightId")
	List<ProjectStraightBomEntity> findByProjectStraight(@Param("projectStraightId") Long projectStraightId);
}
