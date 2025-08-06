package baekgwa.suhoserver.model.branch.bom.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
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
 * PackageName : baekgwa.suhoserver.model.branch.bom.entity
 * FileName    : BranchBomEntity
 * Author      : Baekgwa
 * Date        : 2025-08-05
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-05     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "branch_bom")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BranchBomEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branch_type_id", nullable = false)
	private BranchTypeEntity branchTypeEntity;

	@Column(name = "item_type", nullable = false, columnDefinition = "품목 구분 / 사출, 가공, 구매 등")
	private String itemType;

	@Column(name = "drawing_number", nullable = false, columnDefinition = "도번")
	private String drawingNumber;

	@Column(name = "item_name", nullable = false, columnDefinition = "품명")
	private String itemName;

	@Column(name = "specification", columnDefinition = "규격")
	private String specification;

	@Column(name = "unit_quantity", nullable = false, columnDefinition = "단위 수량")
	private Long unitQuantity;

	@Column(name = "unit", columnDefinition = "단위 / EA, BOX, ROLL")
	private String unit;

	@Column(name = "supplied_material", nullable = false, columnDefinition = "사급 자재 유무")
	private Boolean suppliedMaterial;

	@Builder
	private BranchBomEntity(BranchTypeEntity branchTypeEntity, String itemType, String drawingNumber, String itemName,
		String specification, Long unitQuantity, String unit, Boolean suppliedMaterial) {
		this.branchTypeEntity = branchTypeEntity;
		this.itemType = itemType;
		this.drawingNumber = drawingNumber;
		this.itemName = itemName;
		this.specification = specification;
		this.unitQuantity = unitQuantity;
		this.unit = unit;
		this.suppliedMaterial = suppliedMaterial;
	}
}
