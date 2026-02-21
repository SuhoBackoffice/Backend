package baekgwa.suhoserver.model.project.straight.bom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.project.straight.bom.entity.ProjectStraightBomEntity;

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
}
