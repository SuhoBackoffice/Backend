package baekgwa.suhoserver.model.work.report.straight.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.project.straight.serial.entity.ProjectStraightSerialEntity;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_straight_serial_id", nullable = false)
	private ProjectStraightSerialEntity projectStraightSerial;

	@Builder(access = AccessLevel.PRIVATE)
	private WorkReportStraightSerialEntity(WorkReportStraightEntity workReportStraight,
		ProjectStraightSerialEntity projectStraightSerial) {
		this.workReportStraight = workReportStraight;
		this.projectStraightSerial = projectStraightSerial;
	}

	public static WorkReportStraightSerialEntity of(
		WorkReportStraightEntity workReportStraight,
		ProjectStraightSerialEntity projectStraightSerial
	) {
		return WorkReportStraightSerialEntity
			.builder()
			.workReportStraight(workReportStraight)
			.projectStraightSerial(projectStraightSerial)
			.build();
	}
}
