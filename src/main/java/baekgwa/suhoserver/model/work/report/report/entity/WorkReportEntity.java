package baekgwa.suhoserver.model.work.report.report.entity;

import java.time.LocalDate;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.user.entity.UserEntity;
import baekgwa.suhoserver.model.work.report.WorkReportStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.work.report.report.entity
 * FileName    : WorkReportEntity
 * Author      : Baekgwa
 * Date        : 25. 12. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 23.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "work_report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkReportEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "report_user_id", nullable = false)
	private Long reportUserId;

	@Column(name = "report_user_name", nullable = false)
	private String reportUserName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private ProjectEntity project;

	@Column(name = "work_summary", columnDefinition = "TEXT")
	private String workSummary;

	@Column(name = "work_date", nullable = false)
	private LocalDate workDate;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "status", nullable = false)
	private WorkReportStatus status;

	@Builder(access = AccessLevel.PRIVATE)
	private WorkReportEntity(Long reportUserId, String reportUserName, ProjectEntity project, String workSummary,
		LocalDate workDate, WorkReportStatus status) {
		this.reportUserId = reportUserId;
		this.reportUserName = reportUserName;
		this.project = project;
		this.workSummary = workSummary;
		this.workDate = workDate;
		this.status = status;
	}

	public static WorkReportEntity of(UserEntity user, ProjectEntity project, String workSummary, LocalDate workDate) {
		return WorkReportEntity
			.builder()
			.reportUserId(user.getId())
			.reportUserName(user.getUsername())
			.project(project)
			.workSummary(workSummary)
			.workDate(workDate)
			.status(WorkReportStatus.PENDING)
			.build();
	}
}
