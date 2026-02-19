package baekgwa.suhoserver.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
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

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted = false;

	public void softDelete() {
		this.isDeleted = true;
	}
}
