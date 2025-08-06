package baekgwa.suhoserver.model.branch.type.entity;

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
 * PackageName : baekgwa.suhoserver.model.branch.type.entity
 * FileName    : BranchTypeEntity
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
@Table(name = "branch_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BranchTypeEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "version_id", nullable = false)
	private VersionInfoEntity versionInfoEntity;

	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "version", nullable = false, columnDefinition = "분기레일 BOM 리스트의 버전.")
	private LocalDate version;

	/**
	 * 현재 시간 기준으로, Version 정보를 생성
	 * YYYY-mm-dd
	 * @return version
	 */
	private static LocalDate generateVersion() {
		return LocalDate.now();
	}

	@Builder(access = AccessLevel.PRIVATE)
	private BranchTypeEntity(VersionInfoEntity versionInfoEntity, String code, LocalDate version) {
		this.versionInfoEntity = versionInfoEntity;
		this.code = code;
		this.version = version;
	}

	public static BranchTypeEntity createNewBranchType(VersionInfoEntity versionInfoEntity, String code) {
		return BranchTypeEntity
			.builder()
			.versionInfoEntity(versionInfoEntity)
			.code(code)
			.version(BranchTypeEntity.generateVersion())
			.build();
	}
}
