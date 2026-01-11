package baekgwa.suhoserver.model.work.report.report.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.work.report.WorkReportStatus;
import baekgwa.suhoserver.model.work.report.report.entity.WorkReportEntity;

/**
 * PackageName : baekgwa.suhoserver.model.work.report.report.repository
 * FileName    : WorkReportRepository
 * Author      : Baekgwa
 * Date        : 25. 12. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 23.     Baekgwa               Initial creation
 */
public interface WorkReportRepository extends JpaRepository<WorkReportEntity, Long> {

	@Query("SELECT count(wr) > 0 FROM WorkReportEntity wr "
		+ "where wr.reportUserId = :userId "
		+ "and wr.project.id = :projectId "
		+ "and wr.workDate = :workDate "
		+ "AND wr.status in :statusList ")
	boolean existsDailyReport(
		@Param("userId") Long userId,
		@Param("projectId") Long projectId,
		@Param("workDate") LocalDate workDate,
		@Param("statusList") List<WorkReportStatus> statusList
	);

	@EntityGraph(attributePaths = "project")
	Optional<WorkReportEntity> findWithProjectById(Long id);

	List<WorkReportEntity> findByProjectAndStatus(ProjectEntity project, WorkReportStatus status);

	List<WorkReportEntity> findByProjectAndStatusOrderByWorkDateDesc(ProjectEntity project, WorkReportStatus status);

	List<WorkReportEntity> findByProjectOrderByWorkDateDesc(ProjectEntity project);
}
