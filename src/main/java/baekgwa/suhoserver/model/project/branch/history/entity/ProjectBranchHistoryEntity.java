package baekgwa.suhoserver.model.project.branch.history.entity;

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

	@Column(name = "change_user_id", nullable = false)
	private Long changeUserId;

	@Column(name = "change_user_name", nullable = false)
	private String changeUserName;

	@Column(name = "project_id", nullable = false)
	private Long projectId;

	@Column(name = "project_branch_id", nullable = false)
	private Long projectBranchId;

	@Column(name = "branch_type_id", nullable = false)
	private Long branchTypeId;

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
	private ProjectBranchHistoryEntity(Long changeUserId, String changeUserName, Long projectId, Long projectBranchId,
		Long branchTypeId, String branchSerial, ProjectProductAction action, Long beforeQuantity, Long afterQuantity) {
		this.changeUserId = changeUserId;
		this.changeUserName = changeUserName;
		this.projectId = projectId;
		this.projectBranchId = projectBranchId;
		this.branchTypeId = branchTypeId;
		this.branchSerial = branchSerial;
		this.action = action;
		this.beforeQuantity = beforeQuantity;
		this.afterQuantity = afterQuantity;
	}

	/**
	 * 프로젝트 분기레일 생성 History 생성자 팩토리 메서드
	 * @param changeUserId 회원PK
	 * @param changeUserName 회원 이름
	 * @param projectId 프로젝트 PK
	 * @param projectBranchId 프로젝트 분기레일 PK
	 * @param branchTypeId 분기레일 타입 PK
	 * @param branchSerial 분기레일 serial
	 * @param afterQuantity 생성 수량
	 * @return new ProjectBranchHistoryEntity
	 */
	public static ProjectBranchHistoryEntity create(
		Long changeUserId,
		String changeUserName,
		Long projectId,
		Long projectBranchId,
		Long branchTypeId,
		String branchSerial,
		Long afterQuantity
	) {
		return ProjectBranchHistoryEntity
			.builder()
			.changeUserId(changeUserId)
			.changeUserName(changeUserName)
			.projectId(projectId)
			.projectBranchId(projectBranchId)
			.branchTypeId(branchTypeId)
			.branchSerial(branchSerial)
			.action(ProjectProductAction.CREATED)
			.beforeQuantity(0L)
			.afterQuantity(afterQuantity)
			.build();
	}

	/**
	 * 프로젝트 분기레일 삭제(할당 해제) History 생성자 팩토리 메서드
	 * @param changeUserId 회원PK
	 * @param changeUserName 회원 이름
	 * @param projectId 프로젝트 PK
	 * @param projectBranchId 프로젝트 분기레일 PK
	 * @param branchTypeId 분기레일 타입 PK
	 * @param branchSerial 분기레일 serial
	 * @param beforeQuantity 삭제 전 수량
	 * @return new ProjectBranchHistoryEntity
	 */
	public static ProjectBranchHistoryEntity delete(
		Long changeUserId,
		String changeUserName,
		Long projectId,
		Long projectBranchId,
		Long branchTypeId,
		String branchSerial,
		Long beforeQuantity
	) {
		return ProjectBranchHistoryEntity
			.builder()
			.changeUserId(changeUserId)
			.changeUserName(changeUserName)
			.projectId(projectId)
			.projectBranchId(projectBranchId)
			.branchTypeId(branchTypeId)
			.branchSerial(branchSerial)
			.action(ProjectProductAction.DELETED)
			.beforeQuantity(beforeQuantity)
			.afterQuantity(0L)
			.build();
	}

	/**
	 * 프로젝트 분기레일 업데이트 History 생성자 팩토리 메서드
	 * @param changeUserId 회원PK
	 * @param changeUserName 회원 이름
	 * @param projectId 프로젝트 PK
	 * @param projectBranchId 프로젝트 분기레일 PK
	 * @param branchTypeId 분기레일 타입 PK
	 * @param branchSerial 분기레일 serial
	 * @param beforeQuantity 업데이트 전 수량
	 * @param afterQuantity 업데이트 후 수량
	 * @return new ProjectBranchHistoryEntity
	 */
	public static ProjectBranchHistoryEntity update(
		Long changeUserId,
		String changeUserName,
		Long projectId,
		Long projectBranchId,
		Long branchTypeId,
		String branchSerial,
		Long beforeQuantity,
		Long afterQuantity
	) {
		return ProjectBranchHistoryEntity
			.builder()
			.changeUserId(changeUserId)
			.changeUserName(changeUserName)
			.projectId(projectId)
			.projectBranchId(projectBranchId)
			.branchTypeId(branchTypeId)
			.branchSerial(branchSerial)
			.action(ProjectProductAction.UPDATED)
			.beforeQuantity(beforeQuantity)
			.afterQuantity(afterQuantity)
			.build();
	}
}
