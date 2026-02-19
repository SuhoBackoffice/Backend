package baekgwa.suhoserver.global.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreRemove;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.global.entity
 * FileName    : SoftDeleteEntity
 * Author      : Baekgwa
 * Date        : 26. 2. 19.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 19.     Baekgwa               Initial creation
 */
@Slf4j
@MappedSuperclass
@Getter
public class SoftDeleteEntity extends TemporalEntity {

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted = false;

	public void delete() {
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now();
	}

	@PreRemove
	private void preRemove() {
		log.debug("Pre-Remove Entity");
		this.delete();
	}

	public void restore() {
		this.isDeleted = false;
		this.deletedAt = null;
	}
}
