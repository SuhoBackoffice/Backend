package baekgwa.suhoserver.model.project.branch.branch.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
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
 * PackageName : baekgwa.suhoserver.model.project.branch.branch.entity
 * FileName    : ProjectBranchEntity
 * Author      : Baekgwa
 * Date        : 2025-08-07
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-07     Baekgwa               Initial creation
 */
@Slf4j
@Entity
@Getter
@Table(name = "project_branch")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectBranchEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private ProjectEntity project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branch_type_id", nullable = false)
	private BranchTypeEntity branchType;

	@Column(name = "total_quantity", nullable = false)
	private Long totalQuantity;

	@Column(name = "completed_quantity", nullable = false)
	private Long completedQuantity;

	@Builder(access = AccessLevel.PRIVATE)
	private ProjectBranchEntity(ProjectEntity project, BranchTypeEntity branchType, Long totalQuantity,
		Long completedQuantity) {
		this.project = project;
		this.branchType = branchType;
		this.totalQuantity = totalQuantity;
		this.completedQuantity = completedQuantity;
	}

	public static ProjectBranchEntity createNewProjectBranch(ProjectEntity project, BranchTypeEntity branchType,
		Long totalQuantity) {
		return ProjectBranchEntity.builder()
			.project(project)
			.branchType(branchType)
			.totalQuantity(totalQuantity)
			.completedQuantity(0L)
			.build();
	}

	public void patchProjectBranch(Long totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	/**
	 * 분기 레일 생산 수량 업데이트
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
}
