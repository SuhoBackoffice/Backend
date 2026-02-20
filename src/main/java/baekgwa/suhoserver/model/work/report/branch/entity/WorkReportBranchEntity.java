package baekgwa.suhoserver.model.work.report.branch.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.work.report.report.entity.WorkReportEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * PackageName : baekgwa.suhoserver.model.work.report.branch.entity
 * FileName    : WorkReportBranchEntity
 * Author      : Baekgwa
 * Date        : 26. 1. 2.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 2.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "work_report_branch")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkReportBranchEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_report_id", nullable = false)
	private WorkReportEntity workReport;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_branch_id", nullable = false)
	private ProjectBranchEntity projectBranch;

	@Column(name = "production_quantity")
	private Long productionQuantity;

	@Builder(access = AccessLevel.PRIVATE)
	public WorkReportBranchEntity(WorkReportEntity workReport, ProjectBranchEntity projectBranch,
		Long productionQuantity) {
		this.workReport = workReport;
		this.projectBranch = projectBranch;
		this.productionQuantity = productionQuantity;
	}

	public static WorkReportBranchEntity of(
		WorkReportEntity workReport,
		ProjectBranchEntity projectBranch,
		Long productionQuantity
	) {
		return WorkReportBranchEntity
			.builder()
			.workReport(workReport)
			.projectBranch(projectBranch)
			.productionQuantity(productionQuantity)
			.build();
	}
}
