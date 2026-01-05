package baekgwa.suhoserver.model.work.report.branch.entity;

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
 * FileName    : WorkReportBranchSerialEntity
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
@Table(name = "work_report_branch_serial")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkReportBranchSerialEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_report_branch_id", nullable = false)
	private WorkReportBranchEntity workReportBranch;

	@Column(name = "project_branch_serial_id", nullable = false)
	private Long projectBranchSerialId;

	@Column(name = "project_branch_serial", nullable = false)
	private String serial;

	@Builder(access = AccessLevel.PRIVATE)
	private WorkReportBranchSerialEntity(WorkReportBranchEntity workReportBranch, Long projectBranchSerialId,
		String serial) {
		this.workReportBranch = workReportBranch;
		this.projectBranchSerialId = projectBranchSerialId;
		this.serial = serial;
	}

	public static WorkReportBranchSerialEntity of(
		WorkReportBranchEntity workReportBranch,
		Long projectBranchSerialId,
		String serial
	) {
		return WorkReportBranchSerialEntity
			.builder()
			.workReportBranch(workReportBranch)
			.projectBranchSerialId(projectBranchSerialId)
			.serial(serial)
			.build();
	}
}
