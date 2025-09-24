package baekgwa.suhoserver.model.material.inbound.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.material.inbound.entity.MaterialInboundEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;

/**
 * PackageName : baekgwa.suhoserver.model.material.inbound.repository
 * FileName    : MaterialInboundRepository
 * Author      : Baekgwa
 * Date        : 2025-09-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-19     Baekgwa               Initial creation
 */
public interface MaterialInboundRepository extends JpaRepository<MaterialInboundEntity, Long>, MaterialInboundRepositoryCustom {

	List<MaterialInboundEntity> findByProject(ProjectEntity findProject);
}
