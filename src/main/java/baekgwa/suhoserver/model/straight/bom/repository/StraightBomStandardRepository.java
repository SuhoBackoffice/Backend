package baekgwa.suhoserver.model.straight.bom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.straight.bom.entity.StraightBomStandardEntity;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;

/**
 * PackageName : baekgwa.suhoserver.model.straight.bom.repository
 * FileName    : StraightBomStandardRepository
 * Author      : Baekgwa
 * Date        : 26. 2. 20.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 20.     Baekgwa               Initial creation
 */
public interface StraightBomStandardRepository extends JpaRepository<StraightBomStandardEntity, Long> {
	List<StraightBomStandardEntity> findAllByVersionInfo(VersionInfoEntity versionInfo);
}
