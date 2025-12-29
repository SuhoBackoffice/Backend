package baekgwa.suhoserver.model.project.straight.serial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

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

	List<ProjectStraightSerialEntity> findAllByIdIn(List<Long> serialIds);
}
