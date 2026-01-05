package baekgwa.suhoserver.model.work.report.branch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.work.report.branch.entity.WorkReportBranchSerialEntity;

/**
 * PackageName : baekgwa.suhoserver.model.work.report.branch.repository
 * FileName    : WorkReportBranchSerialRepository
 * Author      : Baekgwa
 * Date        : 26. 1. 2.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 2.     Baekgwa               Initial creation
 */
public interface WorkReportBranchSerialRepository extends JpaRepository<WorkReportBranchSerialEntity, Long> {
	boolean existsByProjectBranchSerialId(Long projectBranchSerialId);

	boolean existsByProjectBranchSerialIdIn(List<Long> serialIdList);

	@Query("SELECT wrbs "
		+ "FROM WorkReportBranchSerialEntity wrbs "
		+ "WHERE wrbs.workReportBranch.id in :branchIds")
	List<WorkReportBranchSerialEntity> findAllByBranchIds(@Param("branchIds") List<Long> branchIdList);
}
