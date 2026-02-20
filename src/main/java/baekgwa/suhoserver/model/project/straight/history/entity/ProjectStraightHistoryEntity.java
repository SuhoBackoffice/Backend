package baekgwa.suhoserver.model.project.straight.history.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.project.ProjectProductAction;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.user.entity.UserEntity;
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
 * PackageName : baekgwa.suhoserver.model.project.straight.history.entity
 * FileName    : ProjectStraightHistoryEntity
 * Author      : Baekgwa
 * Date        : 25. 12. 26.
 * Description : 프로젝트 직선레일 변경이력 관리 table
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 26.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "project_straight_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectStraightHistoryEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@JoinColumn(name = "change_user_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private UserEntity changeUser;

	@JoinColumn(name = "project_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private ProjectEntity project;

	@JoinColumn(name = "project_straight_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private ProjectStraightEntity projectStraight;

	@Column(name = "straight_serial", nullable = false)
	private String straightSerial;

	@Enumerated(EnumType.STRING)
	@Column(name = "action", nullable = false)
	private ProjectProductAction action;

	@Column(name = "before_quantity", nullable = false)
	private Long beforeQuantity;

	@Column(name = "after_quantity", nullable = false)
	private Long afterQuantity;

	@Builder(access = AccessLevel.PRIVATE)
	private ProjectStraightHistoryEntity(UserEntity changeUser, ProjectEntity project,
		ProjectStraightEntity projectStraight,
		String straightSerial, ProjectProductAction action, Long beforeQuantity, Long afterQuantity
	) {
		this.changeUser = changeUser;
		this.project = project;
		this.projectStraight = projectStraight;
		this.straightSerial = straightSerial;
		this.action = action;
		this.beforeQuantity = beforeQuantity;
		this.afterQuantity = afterQuantity;
	}

	/**
	 * 프로젝트 직선(루프)레일 생성 히스토리 생성 정적 팩터리 메서드
	 */
	public static ProjectStraightHistoryEntity create(
		UserEntity user,
		ProjectEntity project,
		ProjectStraightEntity projectStraight,
		String straightSerial,
		Long afterQuantity
	) {
		return ProjectStraightHistoryEntity
			.builder()
			.changeUser(user)
			.project(project)
			.projectStraight(projectStraight)
			.straightSerial(straightSerial)
			.beforeQuantity(0L)
			.afterQuantity(afterQuantity)
			.action(ProjectProductAction.CREATED)
			.build();
	}

	/**
	 * 프로젝트 직선(루프)레일 생성 히스토리 생성 정적 팩터리 메서드
	 */
	public static ProjectStraightHistoryEntity delete(
		UserEntity user,
		ProjectEntity project,
		ProjectStraightEntity projectStraight,
		String straightSerial,
		Long beforeQuantity
	) {
		return ProjectStraightHistoryEntity
			.builder()
			.changeUser(user)
			.project(project)
			.projectStraight(projectStraight)
			.straightSerial(straightSerial)
			.beforeQuantity(beforeQuantity)
			.afterQuantity(0L)
			.action(ProjectProductAction.DELETED)
			.build();
	}

	/**
	 * 프로젝트 직선(루프)레일 생성 히스토리 생성 정적 팩터리 메서드
	 */
	public static ProjectStraightHistoryEntity update(
		UserEntity user,
		ProjectEntity project,
		ProjectStraightEntity projectStraight,
		String straightSerial,
		Long beforeQuantity,
		Long afterQuantity
	) {
		return ProjectStraightHistoryEntity
			.builder()
			.changeUser(user)
			.project(project)
			.projectStraight(projectStraight)
			.straightSerial(straightSerial)
			.beforeQuantity(beforeQuantity)
			.afterQuantity(afterQuantity)
			.action(ProjectProductAction.UPDATED)
			.build();
	}
}
