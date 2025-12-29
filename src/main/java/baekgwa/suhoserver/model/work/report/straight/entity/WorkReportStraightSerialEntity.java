package baekgwa.suhoserver.model.work.report.straight.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
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
 * FileName    : WorkReportStraightSerialEntity
 * Author      : Baekgwa
 * Date        : 25. 12. 29.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 29.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "work_report_straight_serial")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkReportStraightSerialEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_report_straight_id", nullable = false)
	private WorkReportStraightEntity workReportStraight;

	@Column(name = "project_straight_serial_id", nullable = false)
	private Long projectStraightSerialId;

	@Builder(access = AccessLevel.PRIVATE)
	private WorkReportStraightSerialEntity(WorkReportStraightEntity workReportStraight, Long projectStraightSerialId) {
		this.workReportStraight = workReportStraight;
		this.projectStraightSerialId = projectStraightSerialId;
	}

	public static WorkReportStraightSerialEntity of(
		WorkReportStraightEntity workReportStraight,
		Long projectStraightSerialId
	) {
		return WorkReportStraightSerialEntity
			.builder()
			.workReportStraight(workReportStraight)
			.projectStraightSerialId(projectStraightSerialId)
			.build();
	}
}
