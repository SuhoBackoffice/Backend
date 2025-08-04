package baekgwa.suhoserver.model.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.user.entity
 * FileName    : UserRole
 * Author      : Baekgwa
 * Date        : 2025-08-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-02     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
	USER("직원"),
	STAFF("관리자"),
	ADMIN("admin");

	private final String description;
}
