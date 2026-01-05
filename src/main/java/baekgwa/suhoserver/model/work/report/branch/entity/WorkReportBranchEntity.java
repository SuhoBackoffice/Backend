package baekgwa.suhoserver.model.work.report.branch.entity;

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
public class WorkReportBranchEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_report_id", nullable = false)
	private WorkReportEntity workReport;

	@Column(name = "project_branch_id")
	private Long projectBranchId;

	@Column(name = "production_quantity")
	private Long productionQuantity;

	@Column(name = "snapshot_serial", nullable = false)
	private String serial;

	@Builder(access = AccessLevel.PRIVATE)
	private WorkReportBranchEntity(WorkReportEntity workReport, Long projectBranchId, Long productionQuantity,
		String serial) {
		this.workReport = workReport;
		this.projectBranchId = projectBranchId;
		this.productionQuantity = productionQuantity;
		this.serial = serial;
	}

	public static WorkReportBranchEntity of(
		WorkReportEntity workReport,
		Long projectBranchId,
		Long productionQuantity,
		String serial
	){
		return WorkReportBranchEntity
			.builder()
			.workReport(workReport)
			.projectBranchId(projectBranchId)
			.productionQuantity(productionQuantity)
			.serial(serial)
			.build();
	}
}
