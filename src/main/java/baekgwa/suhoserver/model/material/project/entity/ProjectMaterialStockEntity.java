package baekgwa.suhoserver.model.material.project.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
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
 * PackageName : baekgwa.suhoserver.model.material.project.entity
 * FileName    : ProjectMaterialStockEntity
 * Author      : Baekgwa
 * Date        : 26. 2. 18.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 18.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "project_material_stock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMaterialStockEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private ProjectEntity project;

	@Column(name = "material_code", nullable = false, columnDefinition = "도번 혹은 식별자")
	private String materialCode;

	@Column(name = "item_name", nullable = false, columnDefinition = "품명")
	private String itemName;

	@Column(name = "total_plan_quantity", nullable = false)
	private Long totalPlanQuantity;

	@Column(name = "total_inbound_quantity", nullable = false)
	private Long totalInboundQuantity;

	@Column(name = "total_used_quantity", nullable = false)
	private Long totalUsedQuantity;

	@Builder(access = AccessLevel.PRIVATE)
	private ProjectMaterialStockEntity(ProjectEntity project, String materialCode, String itemName,
		Long totalPlanQuantity,
		Long totalInboundQuantity, Long totalUsedQuantity) {
		this.project = project;
		this.materialCode = materialCode;
		this.itemName = itemName;
		this.totalPlanQuantity = totalPlanQuantity;
		this.totalInboundQuantity = totalInboundQuantity;
		this.totalUsedQuantity = totalUsedQuantity;
	}

	public static ProjectMaterialStockEntity createNewBranchStock(ProjectEntity project, String drawingNumber,
		String itemName, Long totalPlanQuantity) {
		return ProjectMaterialStockEntity
			.builder()
			.project(project)
			.materialCode(drawingNumber)
			.itemName(itemName)
			.totalPlanQuantity(totalPlanQuantity)
			.totalInboundQuantity(0L)
			.totalUsedQuantity(0L)
			.build();
	}

	public static ProjectMaterialStockEntity createNewStraightStock(ProjectEntity project, String materialCode,
		String itemName, Long totalPlanQuantity) {
		return ProjectMaterialStockEntity
			.builder()
			.project(project)
			.materialCode(materialCode)
			.itemName(itemName)
			.totalPlanQuantity(totalPlanQuantity)
			.totalInboundQuantity(0L)
			.totalUsedQuantity(0L)
			.build();
	}

	public void addTotalPlanQuantity(Long addQuantity) {
		this.totalPlanQuantity += addQuantity;
	}

	public void addTotalUsedQuantity(Long usedQuantity) {
		this.totalUsedQuantity += usedQuantity;
	}

	public void addTotalInboundQuantity(Long inboundQuantity) {
		this.totalInboundQuantity += inboundQuantity;
	}
}
