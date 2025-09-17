package baekgwa.suhoserver.model.project.straight.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.straight.info.entity.StraightInfoEntity;
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

/**
 * PackageName : baekgwa.suhoserver.model.project.straight.entity
 * FileName    : ProjectStraightEntity
 * Author      : Baekgwa
 * Date        : 2025-08-08
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-08     Baekgwa               Initial creation
 */
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "straight_info_id", nullable = false)
	private StraightInfoEntity straightInfo;

	@Column(name = "total_quantity", nullable = false)
	private Long totalQuantity;

	@Column(name = "completed_quantity", nullable = false)
	private Long completedQuantity;

	@Column(name = "is_loop_rail", nullable = false)
	private Boolean isLoopRail;

	@Column(name = "length", nullable = false)
	private Long length;

	@Builder
	private ProjectStraightEntity(ProjectEntity project, StraightTypeEntity straightType, Long totalQuantity,
		Long completedQuantity, Boolean isLoopRail, Long length, StraightInfoEntity straightInfo
	) {
		this.project = project;
		this.straightType = straightType;
		this.totalQuantity = totalQuantity;
		this.completedQuantity = completedQuantity;
		this.isLoopRail = isLoopRail;
		this.length = length;
		this.straightInfo = straightInfo;
	}

	public static ProjectStraightEntity createNewStraight(
		ProjectEntity project, StraightTypeEntity straightType, Long totalQuantity, Boolean isLoopRail, Long length, StraightInfoEntity straightInfo
	) {
		return ProjectStraightEntity
			.builder()
			.project(project)
			.straightType(straightType)
			.totalQuantity(totalQuantity)
			.completedQuantity(0L)
			.isLoopRail(isLoopRail)
			.length(length)
			.straightInfo(straightInfo)
			.build();
	}

	/**
	 * 프로젝트 직선레일 정보 업데이트용 편의 메서드
	 * - 현재, 수량만 변경 가능하도록 구성.
	 */
	public void patchProjectStraight(Long totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
}
