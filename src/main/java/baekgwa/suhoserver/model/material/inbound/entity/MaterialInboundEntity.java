package baekgwa.suhoserver.model.material.inbound.entity;

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
 * PackageName : baekgwa.suhoserver.model.material.inbound.entity
 * FileName    : MaterialInboundEntity
 * Author      : Baekgwa
 * Date        : 2025-09-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-19     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "material_inbound")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MaterialInboundEntity extends TemporalEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "drawing_number", nullable = false, columnDefinition = "도번")
	private String drawingNumber;

	@Column(name = "item_name", nullable = false, columnDefinition = "품명")
	private String itemName;

	@Column(name = "quantity", nullable = false, columnDefinition = "입고 수량")
	private Long quantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private ProjectEntity project;

	@Builder(access = AccessLevel.PRIVATE)
	private MaterialInboundEntity(String drawingNumber, String itemName, Long quantity, ProjectEntity project) {
		this.drawingNumber = drawingNumber;
		this.itemName = itemName;
		this.quantity = quantity;
		this.project = project;
	}

	public static MaterialInboundEntity of(String drawingNumber, String itemName, Long quantity, ProjectEntity project) {
		return MaterialInboundEntity
			.builder()
			.drawingNumber(drawingNumber)
			.itemName(itemName)
			.quantity(quantity)
			.project(project)
			.build();
	}
}
