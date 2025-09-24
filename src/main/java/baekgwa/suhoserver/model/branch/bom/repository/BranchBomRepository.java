package baekgwa.suhoserver.model.branch.bom.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;

/**
 * PackageName : baekgwa.suhoserver.model.branch.bom.repository
 * FileName    : BranchBomRepository
 * Author      : Baekgwa
 * Date        : 2025-08-05
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-05     Baekgwa               Initial creation
 */
public interface BranchBomRepository extends JpaRepository<BranchBomEntity, Long>, BranchBomRepositoryCustom {

	List<BranchBomEntity> findByBranchTypeEntity(BranchTypeEntity branchTypeEntity);

	@Query("SELECT b FROM BranchBomEntity b WHERE b.branchTypeEntity.id in :typeIds")
	List<BranchBomEntity> findAllByBranchTypeIds(@Param("typeIds") Collection<Long> typeIds);

}
