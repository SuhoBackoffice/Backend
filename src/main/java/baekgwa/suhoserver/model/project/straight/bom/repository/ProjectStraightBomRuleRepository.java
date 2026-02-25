package baekgwa.suhoserver.model.project.straight.bom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.bom.entity.ProjectStraightBomRuleEntity;

/**
 * PackageName : baekgwa.suhoserver.model.project.straight.bom.repository
 * FileName    : ProjectStraightBomRuleRepository
 * Author      : Baekgwa
 * Date        : 26. 2. 21.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 21.     Baekgwa               Initial creation
 */
public interface ProjectStraightBomRuleRepository extends JpaRepository<ProjectStraightBomRuleEntity, Long> {
	List<ProjectStraightBomRuleEntity> findAllByProject(ProjectEntity project);
}
