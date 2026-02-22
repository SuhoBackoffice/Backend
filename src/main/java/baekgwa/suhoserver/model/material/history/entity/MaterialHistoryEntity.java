package baekgwa.suhoserver.model.material.history.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.material.MaterialHistoryType;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
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
 * PackageName : baekgwa.suhoserver.model.material.history.entity
 * FileName    : MaterialHistoryEntity
 * Author      : Baekgwa
 * Date        : 26. 2. 18.
 * Description : 자재 입고 원장 기록
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 18.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "material_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MaterialHistoryEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "material_code", nullable = false, columnDefinition = "도번 혹은 식별자")
	private String materialCode;

	@Column(name = "item_name", nullable = false, columnDefinition = "품명")
	private String itemName;

	@Column(name = "quantity", nullable = false, columnDefinition = "변동 수량")
	private Long quantity;

	@Column(name = "description", nullable = false, columnDefinition = "변동에 대한 설명")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private ProjectEntity project;

	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private MaterialHistoryType type;

	@Builder(access = AccessLevel.PRIVATE)
	private MaterialHistoryEntity(String materialCode, String itemName, Long quantity, String description,
		ProjectEntity project, MaterialHistoryType type) {
		this.materialCode = materialCode;
		this.itemName = itemName;
		this.quantity = quantity;
		this.description = description;
		this.project = project;
		this.type = type;
	}

	public static MaterialHistoryEntity createNewHistory(
		ProjectEntity project,
		String materialCode,
		String itemName,
		Long quantity,
		MaterialHistoryType type,
		String description
	) {
		return MaterialHistoryEntity.builder()
			.project(project)
			.materialCode(materialCode)
			.itemName(itemName)
			.quantity(quantity)
			.type(type)
			.description(description)
			.build();
	}
}
