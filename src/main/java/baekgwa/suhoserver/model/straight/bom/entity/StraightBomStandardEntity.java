package baekgwa.suhoserver.model.straight.bom.entity;

import java.math.BigDecimal;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.straight.StraightBomConditionType;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
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
 * PackageName : baekgwa.suhoserver.model.straight.bom.entity
 * FileName    : StraightBomStandardEntity
 * Author      : Baekgwa
 * Date        : 26. 2. 20.
 * Description : 버전별 직선레일 표준 BOM List 산출 규칙
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 20.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "straight_bom_standard")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StraightBomStandardEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "version_id", nullable = false)
	private VersionInfoEntity versionInfo;

	@Enumerated(EnumType.STRING)
	@Column(name = "condition_type", nullable = false)
	private StraightBomConditionType conditionType;

	// 이상
	@Column(name = "min_condition_value")
	private BigDecimal minConditionValue;

	// 미만
	@Column(name = "max_condition_value")
	private BigDecimal maxConditionValue;

	@Column(name = "material_code", nullable = false)
	private String materialCode;

	@Column(name = "item_name", nullable = false)
	private String itemName;

	@Column(name = "quantity", nullable = false)
	private Long quantity;

	@Builder(access = AccessLevel.PRIVATE)
	private StraightBomStandardEntity(VersionInfoEntity versionInfo, StraightBomConditionType conditionType,
		BigDecimal minConditionValue, BigDecimal maxConditionValue, String materialCode, String itemName,
		Long quantity) {
		this.versionInfo = versionInfo;
		this.conditionType = conditionType;
		this.minConditionValue = minConditionValue;
		this.maxConditionValue = maxConditionValue;
		this.materialCode = materialCode;
		this.itemName = itemName;
		this.quantity = quantity;
	}
}
