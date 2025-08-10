package baekgwa.suhoserver.model.straight.type.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.straight.type.entity.StraightTypeEntity;

/**
 * PackageName : baekgwa.suhoserver.model.straight.type.repository
 * FileName    : StraightTypeRepository
 * Author      : Baekgwa
 * Date        : 2025-08-09
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-09     Baekgwa               Initial creation
 */
public interface StraightTypeRepository extends JpaRepository<StraightTypeEntity, Long> {
	boolean existsByType(String type);

	List<StraightTypeEntity> findByIsLoopRail(Boolean isLoopRail);
}
