package baekgwa.suhoserver.model.material.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.material.history.entity.MaterialHistoryEntity;

/**
 * PackageName : baekgwa.suhoserver.model.material.history.repository
 * FileName    : MaterialHistoryRepository
 * Author      : Baekgwa
 * Date        : 26. 2. 18.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 18.     Baekgwa               Initial creation
 */
public interface MaterialHistoryRepository extends JpaRepository<MaterialHistoryEntity, Long>, MaterialHistoryRepositoryCustom {
}
