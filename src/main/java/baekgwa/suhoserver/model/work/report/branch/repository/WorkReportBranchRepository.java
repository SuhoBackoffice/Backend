package baekgwa.suhoserver.model.work.report.branch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.work.report.branch.entity.WorkReportBranchEntity;

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
}
