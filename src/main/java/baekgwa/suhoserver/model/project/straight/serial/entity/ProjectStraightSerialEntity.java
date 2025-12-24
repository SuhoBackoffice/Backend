package baekgwa.suhoserver.model.project.straight.serial.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.global.factory.ProductSerialFactory;
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
import lombok.Builder;
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
	@JoinColumn(name = "project_straight_id", nullable = false)
	private ProjectStraightEntity projectStraight;

	@Enumerated(EnumType.STRING)
	@Column(name = "state", nullable = false)
	private ProductSerialState state;

	@Enumerated(EnumType.STRING)
	@Column(name = "reason", nullable = false)
	private ProductInactiveReason reason;

	@Column(name = "sequence", nullable = false)
	private Long sequence;

	@Builder(access = AccessLevel.PRIVATE)
	private ProjectStraightSerialEntity(String serial, ProjectStraightEntity projectStraight, ProductSerialState state,
		ProductInactiveReason reason, Long sequence) {
		this.serial = serial;
		this.projectStraight = projectStraight;
		this.state = state;
		this.reason = reason;
		this.sequence = sequence;
	}

	/**
	 * 프로젝트에 할당된 직선레일의 serial Entity 생성 정적 팩터리 메서드
	 * @param projectStraight 프로젝트에 할당된 직선레일
	 * @param sequence 시퀸스 번호
	 * @return ProjectStraightSerialEntity
	 */
	public static ProjectStraightSerialEntity of(ProjectStraightEntity projectStraight, long sequence) {

		String serial = ProductSerialFactory.generateStraightSerial(
			projectStraight.getLength(),
			projectStraight.getIsLoopRail(),
			projectStraight.getStraightType().getType(),
			sequence
		);

		return ProjectStraightSerialEntity
			.builder()
			.serial(serial)
			.projectStraight(projectStraight)
			.state(ProductSerialState.ACTIVE)
			.sequence(sequence)
			.build();
	}

	/**
	 * Serial 의 상태를 비활성화 하는 메서드
	 * @param reason 비활성화 사유
	 */
	public void deactivate(ProductInactiveReason reason) {
		this.state = ProductSerialState.INACTIVE;
		this.reason = reason;
	}

	/**
	 * Serial 의 상태를 활성화 하는 메서드
	 * 기존에 있던, 비활성화 사유 this.reason 은 null 처리
	 */
	public void activate() {
		this.state = ProductSerialState.ACTIVE;
		this.reason = null;
	}
}
