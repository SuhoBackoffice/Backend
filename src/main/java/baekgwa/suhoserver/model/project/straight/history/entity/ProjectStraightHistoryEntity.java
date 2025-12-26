package baekgwa.suhoserver.model.project.straight.history.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.project.ProjectProductAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	@Column(name = "change_user_id", nullable = false)
	private Long changeUserId;

	@Column(name = "change_user_name", nullable = false)
	private String changeUserName;

	@Column(name = "project_id", nullable = false)
	private Long projectId;

	@Column(name = "project_straight_id", nullable = false)
	private Long projectStraightId;

	@Column(name = "straight_serial", nullable = false)
	private String straightSerial;

	@Enumerated(EnumType.STRING)
	@Column(name = "action", nullable = false)
	private ProjectProductAction action;

	@Column(name = "before_quantity", nullable = false)
	private Long beforeQuantity;

	@Column(name = "after_quantity", nullable = false)
	private Long afterQuantity;

	@Builder
	private ProjectStraightHistoryEntity(Long changeUserId, String changeUserName, Long projectId,
		Long projectStraightId,
		String straightSerial, ProjectProductAction action, Long beforeQuantity, Long afterQuantity) {
		this.changeUserId = changeUserId;
		this.changeUserName = changeUserName;
		this.projectId = projectId;
		this.projectStraightId = projectStraightId;
		this.straightSerial = straightSerial;
		this.action = action;
		this.beforeQuantity = beforeQuantity;
		this.afterQuantity = afterQuantity;
	}

	/**
	 * 프로젝트 직선(루프)레일 생성 히스토리 생성 정적 팩터리 메서드
	 * @param userId 회원 pk
	 * @param userName 회원 이름
	 * @param projectId 프로젝트 pk
	 * @param projectStraightId 프로젝트 직선레일 pk
	 * @param straightSerial 직선레일 식별자
	 * @param afterQuantity 이후 수량
	 * @return new ProjectStraightHistoryEntity
	 */
	public static ProjectStraightHistoryEntity create(
		Long userId,
		String userName,
		Long projectId,
		Long projectStraightId,
		String straightSerial,
		Long afterQuantity
	) {
		return ProjectStraightHistoryEntity
			.builder()
			.changeUserId(userId)
			.changeUserName(userName)
			.projectId(projectId)
			.projectStraightId(projectStraightId)
			.straightSerial(straightSerial)
			.beforeQuantity(0L)
			.afterQuantity(afterQuantity)
			.action(ProjectProductAction.CREATED)
			.build();
	}

	public static ProjectStraightHistoryEntity delete(
		Long userId,
		String userName,
		Long projectId,
		Long projectStraightId,
		String straightSerial,
		Long beforeQuantity
	) {
		return ProjectStraightHistoryEntity
			.builder()
			.changeUserId(userId)
			.changeUserName(userName)
			.projectId(projectId)
			.projectStraightId(projectStraightId)
			.straightSerial(straightSerial)
			.beforeQuantity(beforeQuantity)
			.afterQuantity(0L)
			.action(ProjectProductAction.DELETED)
			.build();
	}
}
