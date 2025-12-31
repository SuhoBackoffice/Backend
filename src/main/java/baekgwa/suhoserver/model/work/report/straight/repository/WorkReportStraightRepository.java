package baekgwa.suhoserver.model.work.report.straight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.work.report.WorkReportStatus;
import baekgwa.suhoserver.model.work.report.report.entity.WorkReportEntity;
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

	List<WorkReportStraightEntity> findByWorkReportIn(List<WorkReportEntity> workReportList);

	@Query("SELECT wrss.projectStraightSerialId "
		+ "FROM WorkReportStraightSerialEntity wrss "
		+ "JOIN wrss.workReportStraight wrs "
		+ "JOIN wrs.workReport wr "
		+ "WHERE wr.status = :status "
		+ "AND wrs.projectStraightId = :projectStraightId")
	List<Long> findPendingSerialIdList(
		@Param("status") WorkReportStatus status,
		@Param("projectStraightId") Long projectStraightId
	);
}
