package baekgwa.suhoserver.model.project.straight.bom.entity;

import java.math.BigDecimal;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.straight.StraightBomConditionType;
import baekgwa.suhoserver.model.straight.bom.entity.StraightBomStandardEntity;
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
 * PackageName : baekgwa.suhoserver.model.project.straight.bom.entity
 * FileName    : ProjectStraightBomRuleEntity
 * Author      : Baekgwa
 * Date        : 26. 2. 21.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 21.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "project_straight_bom_rule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectStraightBomRuleEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private ProjectEntity project;

	@Enumerated(EnumType.STRING)
	@Column(name = "condition_type", nullable = false)
	private StraightBomConditionType conditionType;

	@Column(name = "min_condition_value", nullable = false)
	private BigDecimal minConditionValue;

	@Column(name = "max_condition_value", nullable = false)
	private BigDecimal maxConditionValue;

	@Column(name = "material_code", nullable = false)
	private String materialCode;

	@Column(name = "item_name", nullable = false)
	private String itemName;

	@Column(name = "quantity", nullable = false)
	private Long quantity;

	@Builder(access = AccessLevel.PRIVATE)
	private ProjectStraightBomRuleEntity(ProjectEntity project, StraightBomConditionType conditionType,
		BigDecimal minConditionValue, BigDecimal maxConditionValue, String materialCode, String itemName,
		Long quantity) {
		this.project = project;
		this.conditionType = conditionType;
		this.minConditionValue = minConditionValue;
		this.maxConditionValue = maxConditionValue;
		this.materialCode = materialCode;
		this.itemName = itemName;
		this.quantity = quantity;
	}

	public static ProjectStraightBomRuleEntity of(ProjectEntity project, StraightBomStandardEntity standard) {
		return ProjectStraightBomRuleEntity
			.builder()
			.project(project)
			.conditionType(standard.getConditionType())
			.minConditionValue(standard.getMinConditionValue())
			.maxConditionValue(standard.getMaxConditionValue())
			.materialCode(standard.getMaterialCode())
			.itemName(standard.getItemName())
			.quantity(standard.getQuantity())
			.build();
	}
}