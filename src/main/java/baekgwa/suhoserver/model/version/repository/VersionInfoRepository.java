package baekgwa.suhoserver.model.version.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;

/**
 * PackageName : baekgwa.suhoserver.model.version.repository
 * FileName    : VersionInfoRepository
 * Author      : Baekgwa
 * Date        : 2025-08-05
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-05     Baekgwa               Initial creation
 */
public interface VersionInfoRepository extends JpaRepository<VersionInfoEntity, Long> {
	boolean existsByName(String name);
}
