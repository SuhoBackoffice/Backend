package baekgwa.suhoserver.model.project.project.entity;

import java.time.LocalDate;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
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
 * PackageName : baekgwa.suhoserver.model.project.project.entity
 * FileName    : ProjectEntity
 * Author      : Baekgwa
 * Date        : 2025-08-07
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-07     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "project")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "version_id", nullable = false)
	private VersionInfoEntity versionInfoEntity;

	@Column(name = "region", nullable = false)
	private String region;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Builder(access = AccessLevel.PRIVATE)
	private ProjectEntity(VersionInfoEntity versionInfoEntity, String region, String name, LocalDate startDate,
		LocalDate endDate) {
		this.versionInfoEntity = versionInfoEntity;
		this.region = region;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public static ProjectEntity createNewProject(VersionInfoEntity versionInfoEntity, String name, String region,
		LocalDate startDate, LocalDate endDate) {
		return ProjectEntity
			.builder()
			.versionInfoEntity(versionInfoEntity)
			.region(region)
			.name(name)
			.startDate(startDate)
			.endDate(endDate)
			.build();
	}

}
