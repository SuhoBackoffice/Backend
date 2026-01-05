package baekgwa.suhoserver.model.project.straight.serial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.project.ProductSerialState;
import baekgwa.suhoserver.model.project.straight.serial.entity.ProjectStraightSerialEntity;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;

/**
 * PackageName : baekgwa.suhoserver.model.project.straight.serial.repository
 * FileName    : ProjectStraightSerialRepository
 * Author      : Baekgwa
 * Date        : 25. 12. 23.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 23.     Baekgwa               Initial creation
 */
public interface ProjectStraightSerialRepository extends JpaRepository<ProjectStraightSerialEntity, Long> {

	List<ProjectStraightSerialEntity> findByProjectStraightOrderBySequenceDesc(ProjectStraightEntity projectStraight);

	@EntityGraph(attributePaths = "projectStraight")
	List<ProjectStraightSerialEntity> findAllByIdIn(List<Long> serialIds);

	@Query("SELECT pss FROM ProjectStraightSerialEntity pss "
		+ "WHERE pss.projectStraight.id = :straightId "
		+ "AND pss.state = :state")
	List<ProjectStraightSerialEntity> findProjectStraightSerialList(
		@Param("straightId") Long straightId, @Param("state") ProductSerialState state);
}
