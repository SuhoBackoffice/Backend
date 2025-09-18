package baekgwa.suhoserver.model.straight.info.entity;

import java.math.BigDecimal;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.straight.info.entity
 * FileName    : StraightInfoEntity
 * Author      : Baekgwa
 * Date        : 2025-09-17
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-17     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "straight_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StraightInfoEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "hole_position", precision = 4, scale = 1)
	private BigDecimal holePosition;

	@Column(name = "litzwire1", precision = 4, scale = 1)
	private BigDecimal litzwire1;

	@Column(name = "litzwire2", precision = 4, scale = 1)
	private BigDecimal litzwire2;

	@Column(name = "litzwire3", precision = 4, scale = 1)
	private BigDecimal litzwire3;

	@Column(name = "litzwire4", precision = 4, scale = 1)
	private BigDecimal litzwire4;

	@Column(name = "litzwire5", precision = 4, scale = 1)
	private BigDecimal litzwire5;

	@Column(name = "litzwire6", precision = 4, scale = 1)
	private BigDecimal litzwire6;

	@Builder(access = AccessLevel.PRIVATE)
	private StraightInfoEntity(BigDecimal holePosition, BigDecimal litzwire1, BigDecimal litzwire2,
		BigDecimal litzwire3, BigDecimal litzwire4, BigDecimal litzwire5, BigDecimal litzwire6) {
		this.holePosition = holePosition;
		this.litzwire1 = litzwire1;
		this.litzwire2 = litzwire2;
		this.litzwire3 = litzwire3;
		this.litzwire4 = litzwire4;
		this.litzwire5 = litzwire5;
		this.litzwire6 = litzwire6;
	}

	public static StraightInfoEntity of(BigDecimal holePosition, BigDecimal... wires) {
		if (holePosition == null) {
			throw new IllegalArgumentException("holePosition must not be null");
		}
		if (wires == null) wires = new BigDecimal[0];
		if (wires.length > 6) {
			throw new IllegalArgumentException("LitzWire supporter size must be <= 6");
		}

		return StraightInfoEntity.builder()
			.holePosition(holePosition)
			.litzwire1(getOrNull(wires, 0))
			.litzwire2(getOrNull(wires, 1))
			.litzwire3(getOrNull(wires, 2))
			.litzwire4(getOrNull(wires, 3))
			.litzwire5(getOrNull(wires, 4))
			.litzwire6(getOrNull(wires, 5))
			.build();
	}

	private static BigDecimal getOrNull(BigDecimal[] arr, int idx) {
		return (arr != null && idx < arr.length) ? arr[idx] : null;
	}
}
