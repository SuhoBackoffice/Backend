package baekgwa.suhoserver.model.work.report.straight.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.work.report.straight.entity.WorkReportStraightEntity;

/**
 * PackageName : baekgwa.suhoserver.model.work.report.straight.repository
 * FileName    : WorkReportStraightRepository
 * Author      : Baekgwa
 * Date        : 25. 12. 28.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 28.     Baekgwa               Initial creation
 */
public interface WorkReportStraightRepository extends JpaRepository<WorkReportStraightEntity, Long> {
}
