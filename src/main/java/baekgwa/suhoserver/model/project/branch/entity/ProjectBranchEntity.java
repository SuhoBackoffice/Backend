package baekgwa.suhoserver.model.project.branch.entity;

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

/**
 * PackageName : baekgwa.suhoserver.model.project.branch.entity
 * FileName    : ProjectBranchEntity
 * Author      : Baekgwa
 * Date        : 2025-08-07
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-07     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "project_branch")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectBranchEntity {

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

	@Column(name = "target_quantity", nullable = false)
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
}
