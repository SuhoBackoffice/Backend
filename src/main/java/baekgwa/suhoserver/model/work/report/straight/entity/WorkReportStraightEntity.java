package baekgwa.suhoserver.model.work.report.straight.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
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
 * PackageName : baekgwa.suhoserver.model.work.report.straight.entity
 * FileName    : WorkReportStraightEntity
 * Author      : Baekgwa
 * Date        : 25. 12. 28.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 28.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "work_report_straight")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkReportStraightEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_report_id", nullable = false)
	private WorkReportEntity workReport;

	@Column(name = "project_straight_id")
	private Long projectStraightId;

	@Column(name = "production_quantity")
	private Long productionQuantity;

	@Builder(access = AccessLevel.PRIVATE)
	private WorkReportStraightEntity(WorkReportEntity workReport, Long projectStraightId, Long productionQuantity) {
		this.workReport = workReport;
		this.projectStraightId = projectStraightId;
		this.productionQuantity = productionQuantity;
	}

	public static WorkReportStraightEntity of(
		WorkReportEntity workReport,
		Long projectStraightId,
		Long productionQuantity
	) {
		return WorkReportStraightEntity
			.builder()
			.workReport(workReport)
			.projectStraightId(projectStraightId)
			.productionQuantity(productionQuantity)
			.build();
	}
}
