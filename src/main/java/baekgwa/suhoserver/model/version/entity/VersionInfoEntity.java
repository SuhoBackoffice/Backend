package baekgwa.suhoserver.model.version.entity;

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
 * PackageName : baekgwa.suhoserver.model.version
 * FileName    : VersionInfoEntity
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
@Table(name = "version_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VersionInfoEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(unique = true, nullable = false, columnDefinition = "버전 정보")
	private String name;

	@Column(name = "loop_litz_wire", nullable = false, precision = 4, scale = 1)
	private BigDecimal loopLitzWire;

	@Builder(access = AccessLevel.PRIVATE)
	private VersionInfoEntity(String name, BigDecimal loopLitzWire) {
		this.name = name;
		this.loopLitzWire = loopLitzWire;
	}

	public static VersionInfoEntity of(String name, BigDecimal loopLitzWire) {
		return VersionInfoEntity
			.builder()
			.name(name)
			.loopLitzWire(loopLitzWire)
			.build();
	}
}
