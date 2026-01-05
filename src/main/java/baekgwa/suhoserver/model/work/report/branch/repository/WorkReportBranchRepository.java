package baekgwa.suhoserver.model.work.report.branch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.work.report.WorkReportStatus;
import baekgwa.suhoserver.model.work.report.branch.entity.WorkReportBranchEntity;
import baekgwa.suhoserver.model.work.report.report.entity.WorkReportEntity;

/**
 * PackageName : baekgwa.suhoserver.model.work.report.branch.repository
 * FileName    : WorkReportBranchRepository
 * Author      : Baekgwa
 * Date        : 26. 1. 2.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 2.     Baekgwa               Initial creation
 */
public interface WorkReportBranchRepository extends JpaRepository<WorkReportBranchEntity, Long> {

	List<WorkReportBranchEntity> findByWorkReportIn(List<WorkReportEntity> pendingReportList);

	List<WorkReportBranchEntity> findByWorkReport(WorkReportEntity findWorkReport);

	@Query("SELECT wrbs.projectBranchSerialId "
		+ "FROM WorkReportBranchSerialEntity wrbs "
		+ "JOIN wrbs.workReportBranch wrb "
		+ "JOIN wrb.workReport wr "
		+ "WHERE wr.status = :status "
		+ "AND wrb.projectBranchId = :projectBranchId")
	List<Long> findPendingSerialIdList(
		@Param("status") WorkReportStatus workReportStatus,
		@Param("projectBranchId") Long projectBranchId
	);
}
