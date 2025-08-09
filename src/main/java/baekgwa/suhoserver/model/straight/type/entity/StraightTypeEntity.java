package baekgwa.suhoserver.model.straight.type.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.straight.type.entity
 * FileName    : StraightTypeEntity
 * Author      : Baekgwa
 * Date        : 2025-08-09
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-09     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "straight_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StraightTypeEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "is_loop_rail", nullable = false)
	private Boolean isLoopRail;
}
