package baekgwa.suhoserver.model.work.report.branch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.work.report.WorkReportStatus;
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
	@Query("SELECT wrbs "
		+ "FROM WorkReportBranchSerialEntity wrbs "
		+ "WHERE wrbs.workReportBranch.id in :branchIds")
	List<WorkReportBranchSerialEntity> findAllByBranchIds(@Param("branchIds") List<Long> branchIdList);

	@Query("SELECT wrbs "
		+ "FROM WorkReportBranchSerialEntity wrbs "
		+ "WHERE wrbs.workReportBranch.id "
		+ "IN :workReportBranchIdList")
	List<WorkReportBranchSerialEntity> findAllByWorkReportBranchIn(
		@Param("workReportBranchIdList") List<Long> workReportBranchIdList
	);

	@Query("SELECT COUNT(bs) > 0 "
		+ "FROM WorkReportBranchSerialEntity bs "
		+ "JOIN bs.workReportBranch b "
		+ "JOIN b.workReport r "
		+ "WHERE bs.projectBranchSerialId IN :serialIds "
		+ "AND r.status IN :statuses")
	boolean existsBySerialIdsAndStatuses(
		@Param("serialIds") List<Long> serialIds,
		@Param("statuses") List<WorkReportStatus> statuses
	);
}