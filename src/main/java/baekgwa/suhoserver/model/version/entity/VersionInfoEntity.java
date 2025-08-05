package baekgwa.suhoserver.model.version.entity;

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
	private Long id;

	@Column(unique = true, nullable = false, columnDefinition = "버전 정보")
	private String name;

	@Builder
	private VersionInfoEntity(String name) {
		this.name = name;
	}

	public static VersionInfoEntity of(String name) {
		return VersionInfoEntity
			.builder()
			.name(name)
			.build();
	}
}
