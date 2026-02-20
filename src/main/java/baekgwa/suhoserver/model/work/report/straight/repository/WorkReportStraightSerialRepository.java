package baekgwa.suhoserver.model.work.report.straight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.work.report.WorkReportStatus;
import baekgwa.suhoserver.model.work.report.straight.entity.WorkReportStraightSerialEntity;

/**
 * PackageName : baekgwa.suhoserver.model.work.report.straight.repository
 * FileName    : WorkReportStraightSerialRepository
 * Author      : Baekgwa
 * Date        : 25. 12. 29.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 29.     Baekgwa               Initial creation
 */
public interface WorkReportStraightSerialRepository extends JpaRepository<WorkReportStraightSerialEntity, Long> {
	@Query("SELECT COUNT(ss) > 0 " +
		"FROM WorkReportStraightSerialEntity ss " +
		"JOIN ss.workReportStraight s " +
		"JOIN s.workReport r " +
		"WHERE ss.projectStraightSerial.id IN :serialIds " +
		"AND r.status IN :statuses")
	boolean existsBySerialIdsAndStatuses(
		@Param("serialIds") List<Long> serialIds,
		@Param("statuses") List<WorkReportStatus> statuses
	);

	@Query("SELECT wrss "
		+ "FROM WorkReportStraightSerialEntity wrss "
		+ "WHERE wrss.workReportStraight.id in :straightIds")
	List<WorkReportStraightSerialEntity> findAllByStraightIds(
		@Param("straightIds") List<Long> straightIds);

	@Query("SELECT wrss "
		+ "FROM WorkReportStraightSerialEntity wrss "
		+ "WHERE wrss.workReportStraight.id "
		+ "IN :workReportStraightIdList ")
	List<WorkReportStraightSerialEntity> findAllByWorkReportStraightIn(
		@Param("workReportStraightIdList") List<Long> workReportStraightIdList
	);
}
