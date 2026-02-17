package baekgwa.suhoserver.model.project.straight.straight.entity;

import java.math.BigDecimal;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.straight.type.entity.StraightTypeEntity;
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
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.model.project.straight.straight.entity
 * FileName    : ProjectStraightEntity
 * Author      : Baekgwa
 * Date        : 2025-08-08
 * Description : 프로젝트에 할당된 직선레일 정보 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-08     Baekgwa               Initial creation
 */
@Slf4j
@Entity
@Getter
@Table(name = "project_straight")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectStraightEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private ProjectEntity project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "straight_type_id", nullable = false)
	private StraightTypeEntity straightType;

	@Column(name = "total_quantity", nullable = false)
	private Long totalQuantity;

	@Column(name = "completed_quantity", nullable = false)
	private Long completedQuantity;

	@Column(name = "is_loop_rail", nullable = false)
	private Boolean isLoopRail;

	@Column(name = "length", nullable = false)
	private Long length;

	@Column(name = "hole_position", precision = 4, scale = 1)
	private BigDecimal holePosition;

	@Column(name = "litzwire1", precision = 4, scale = 1)
	private BigDecimal litzwire1;

	@Column(name = "litzwire2", precision = 4, scale = 1)
	private BigDecimal litzwire2;

	@Column(name = "litzwire3", precision = 4, scale = 1)
	private BigDecimal litzwire3;

	@Column(name = "litzwire4", precision = 4, scale = 1)
	private BigDecimal litzwire4;

	@Column(name = "litzwire5", precision = 4, scale = 1)
	private BigDecimal litzwire5;

	@Column(name = "litzwire6", precision = 4, scale = 1)
	private BigDecimal litzwire6;

	@Builder
	private ProjectStraightEntity(ProjectEntity project, StraightTypeEntity straightType, Long totalQuantity,
		Long completedQuantity, Boolean isLoopRail, Long length,
		BigDecimal holePosition, BigDecimal litzwire1, BigDecimal litzwire2,
		BigDecimal litzwire3, BigDecimal litzwire4, BigDecimal litzwire5, BigDecimal litzwire6
	) {
		this.project = project;
		this.straightType = straightType;
		this.totalQuantity = totalQuantity;
		this.completedQuantity = completedQuantity;
		this.isLoopRail = isLoopRail;
		this.length = length;
		this.holePosition = holePosition;
		this.litzwire1 = litzwire1;
		this.litzwire2 = litzwire2;
		this.litzwire3 = litzwire3;
		this.litzwire4 = litzwire4;
		this.litzwire5 = litzwire5;
		this.litzwire6 = litzwire6;
	}

	public static ProjectStraightEntity createNewStraight(
		ProjectEntity project, StraightTypeEntity straightType, Long totalQuantity, Boolean isLoopRail, Long length,
		BigDecimal holePosition, BigDecimal[] wires
	) {
		if (holePosition == null) {
			throw new IllegalArgumentException("holePosition must not be null");
		}
		if (wires == null) wires = new BigDecimal[0];
		if (wires.length > 6) {
			throw new IllegalArgumentException("LitzWire supporter size must be <= 6");
		}

		return ProjectStraightEntity
			.builder()
			.project(project)
			.straightType(straightType)
			.totalQuantity(totalQuantity)
			.completedQuantity(0L)
			.isLoopRail(isLoopRail)
			.length(length)
			.holePosition(holePosition)
			.litzwire1(getOrNull(wires, 0))
			.litzwire2(getOrNull(wires, 1))
			.litzwire3(getOrNull(wires, 2))
			.litzwire4(getOrNull(wires, 3))
			.litzwire5(getOrNull(wires, 4))
			.litzwire6(getOrNull(wires, 5))
			.build();
	}

	/**
	 * 프로젝트 직선레일 정보 업데이트용 편의 메서드
	 * 수량만 변경 가능하도록 구성.
	 * 변경된 수량만큼 return
	 * @param changeQuantity 변경할 수량
	 */
	public void patchProjectStraight(Long changeQuantity) {
		this.totalQuantity = changeQuantity;
	}

	/**
	 * 직선레일 생산 수량 업데이트
	 * @param productionQuantity
	 */
	public void updateCompleteQuantity(Long productionQuantity) {
		long targetQuantity = this.completedQuantity + productionQuantity;
		if(targetQuantity > this.totalQuantity) {
			log.debug("총 수량 {}EA / 총 생산량 {}EA", this.totalQuantity, targetQuantity);
			throw new GlobalException(ErrorCode.REPORT_UPDATE_FAIL_PRODUCTION_QUANTITY_EXCEEDED);
		}
		this.completedQuantity = targetQuantity;
	}

	private static BigDecimal getOrNull(BigDecimal[] arr, int idx) {
		return (arr != null && idx < arr.length) ? arr[idx] : null;
	}
}
