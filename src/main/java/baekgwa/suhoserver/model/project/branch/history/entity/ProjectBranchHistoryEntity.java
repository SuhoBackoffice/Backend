package baekgwa.suhoserver.model.project.branch.history.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.project.ProjectProductAction;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
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
 * PackageName : baekgwa.suhoserver.model.project.branch.history.entity
 * FileName    : ProjectBranchHistoryEntity
 * Author      : Baekgwa
 * Date        : 25. 12. 27.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 27.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "project_branch_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectBranchHistoryEntity extends TemporalEntity {

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

	@JoinColumn(name = "project_branch_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private ProjectBranchEntity projectBranch;

	@Column(name = "branch_serial", nullable = false)
	private String branchSerial;

	@Enumerated(EnumType.STRING)
	@Column(name = "action", nullable = false)
	private ProjectProductAction action;

	@Column(name = "before_quantity", nullable = false)
	private Long beforeQuantity;

	@Column(name = "after_quantity", nullable = false)
	private Long afterQuantity;

	@Builder(access = AccessLevel.PRIVATE)
	private ProjectBranchHistoryEntity(UserEntity changeUser, ProjectEntity project, ProjectBranchEntity projectBranch,
		String branchSerial, ProjectProductAction action, Long beforeQuantity,
		Long afterQuantity) {
		this.changeUser = changeUser;
		this.project = project;
		this.projectBranch = projectBranch;
		this.branchSerial = branchSerial;
		this.action = action;
		this.beforeQuantity = beforeQuantity;
		this.afterQuantity = afterQuantity;
	}

	/**
	 * 프로젝트 분기레일 생성 History 생성자 팩토리 메서드
	 * @param changeUser 회원PK
	 * @param project 프로젝트 PK
	 * @param projectBranch 프로젝트 분기레일 PK
	 * @param branchSerial 분기레일 serial
	 * @param afterQuantity 생성 수량
	 * @return new ProjectBranchHistoryEntity
	 */
	public static ProjectBranchHistoryEntity create(
		UserEntity changeUser,
		ProjectEntity project,
		ProjectBranchEntity projectBranch,
		String branchSerial,
		Long afterQuantity
	) {
		return ProjectBranchHistoryEntity
			.builder()
			.changeUser(changeUser)
			.project(project)
			.projectBranch(projectBranch)
			.branchSerial(branchSerial)
			.action(ProjectProductAction.CREATED)
			.beforeQuantity(0L)
			.afterQuantity(afterQuantity)
			.build();
	}

	/**
	 * 프로젝트 분기레일 삭제(할당 해제) History 생성자 팩토리 메서드
	 * @param changeUser 회원PK
	 * @param project 프로젝트 PK
	 * @param projectBranch 프로젝트 분기레일 PK
	 * @param branchSerial 분기레일 serial
	 * @param beforeQuantity 삭제 전 수량
	 * @return new ProjectBranchHistoryEntity
	 */
	public static ProjectBranchHistoryEntity delete(
		UserEntity changeUser,
		ProjectEntity project,
		ProjectBranchEntity projectBranch,
		String branchSerial,
		Long beforeQuantity
	) {
		return ProjectBranchHistoryEntity
			.builder()
			.changeUser(changeUser)
			.project(project)
			.projectBranch(projectBranch)
			.branchSerial(branchSerial)
			.action(ProjectProductAction.DELETED)
			.beforeQuantity(beforeQuantity)
			.afterQuantity(0L)
			.build();
	}

	/**
	 * 프로젝트 분기레일 업데이트 History 생성자 팩토리 메서드
	 * @param changeUser 회원PK
	 * @param project 프로젝트 PK
	 * @param projectBranch 프로젝트 분기레일 PK
	 * @param branchSerial 분기레일 serial
	 * @param beforeQuantity 업데이트 전 수량
	 * @param afterQuantity 업데이트 후 수량
	 * @return new ProjectBranchHistoryEntity
	 */
	public static ProjectBranchHistoryEntity update(
		UserEntity changeUser,
		ProjectEntity project,
		ProjectBranchEntity projectBranch,
		String branchSerial,
		Long beforeQuantity,
		Long afterQuantity
	) {
		return ProjectBranchHistoryEntity
			.builder()
			.changeUser(changeUser)
			.project(project)
			.projectBranch(projectBranch)
			.branchSerial(branchSerial)
			.action(ProjectProductAction.UPDATED)
			.beforeQuantity(beforeQuantity)
			.afterQuantity(afterQuantity)
			.build();
	}
}
