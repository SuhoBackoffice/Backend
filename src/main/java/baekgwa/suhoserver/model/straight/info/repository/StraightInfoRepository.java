package baekgwa.suhoserver.model.straight.info.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.straight.info.entity.StraightInfoEntity;

/**
 * PackageName : baekgwa.suhoserver.model.straight.info.repository
 * FileName    : StraightInfoRepository
 * Author      : Baekgwa
 * Date        : 2025-09-17
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-17     Baekgwa               Initial creation
 */
public interface StraightInfoRepository extends JpaRepository<StraightInfoEntity, Long> {
}
