package baekgwa.suhoserver.model.project.straight.bom.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
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
 * PackageName : baekgwa.suhoserver.model.project.straight.bom.entity
 * FileName    : ProjectStraightBomEntity
 * Author      : Baekgwa
 * Date        : 26. 2. 21.
 * Description : 프로젝트 직선레일 BOM 정보
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 21.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "project_straight_bom")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectStraightBomEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_straight_id", nullable = false)
	private ProjectStraightEntity projectStraight;

	@Column(name = "material_code", nullable = false)
	private String materialCode;

	@Column(name = "item_name", nullable = false)
	private String itemName;

	@Column(name = "unit_quantity", nullable = false)
	private Long unitQuantity; // 단위 수량

	@Builder(access = AccessLevel.PRIVATE)
	private ProjectStraightBomEntity(ProjectStraightEntity projectStraight, String materialCode, String itemName,
		Long unitQuantity) {
		this.projectStraight = projectStraight;
		this.materialCode = materialCode;
		this.itemName = itemName;
		this.unitQuantity = unitQuantity;
	}

	public static ProjectStraightBomEntity of(ProjectStraightEntity projectStraight, String materialCode,
		String itemName, Long unitQuantity) {
		return ProjectStraightBomEntity.builder()
			.projectStraight(projectStraight)
			.materialCode(materialCode)
			.itemName(itemName)
			.unitQuantity(unitQuantity)
			.build();
	}
}
