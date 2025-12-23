package baekgwa.suhoserver.model.project.straight.serial.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.project.ProductInactiveReason;
import baekgwa.suhoserver.model.project.ProductSerialState;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.project.straight.serial.entity
 * FileName    : ProjectStraightSerialEntity
 * Author      : Baekgwa
 * Date        : 25. 12. 23.
 * Description : 프로젝트에 할당된 직선레일의 시리얼 정보 / 프로젝트에 직선레일 할당 시, 수량만큼 생성
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 23.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "project_straight_serial")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectStraightSerialEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "serial", nullable = false)
	private String serial;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_straight_id")
	private ProjectStraightEntity projectStraight;

	@Enumerated(EnumType.STRING)
	@Column(name = "state", nullable = false)
	private ProductSerialState state;

	@Enumerated(EnumType.STRING)
	@Column(name = "reason", nullable = false)
	private ProductInactiveReason reason;
}
