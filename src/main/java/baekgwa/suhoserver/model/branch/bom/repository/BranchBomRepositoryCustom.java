package baekgwa.suhoserver.model.branch.bom.repository;

import java.util.List;

import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;

/**
 * PackageName : baekgwa.suhoserver.model.branch.bom.repository
 * FileName    : BranchBomRepositoryCustom
 * Author      : Baekgwa
 * Date        : 2025-09-20
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-20     Baekgwa               Initial creation
 */
public interface BranchBomRepositoryCustom {

	List<BranchBomEntity> searchBranchBomList(List<Long> findBranchTypeIdList, String keyword);
}
