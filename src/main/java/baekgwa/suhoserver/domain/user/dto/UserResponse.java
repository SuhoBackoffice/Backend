package baekgwa.suhoserver.domain.user.dto;

import baekgwa.suhoserver.model.user.entity.UserRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.user.dto
 * FileName    : UserResponse
 * Author      : Baekgwa
 * Date        : 2025-08-03
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-03     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

	@Getter
	public static class UserInfoDto {
		private final Long id;
		private final String username;
		private final String role;

		@Builder
		private UserInfoDto(Long id, String username, String role) {
			this.id = id;
			this.username = username;
			this.role = role;
		}

		public static UserInfoDto of(Long id, String username, UserRole role) {
			return UserInfoDto
				.builder()
				.id(id)
				.username(username)
				.role(role.getDescription())
				.build();
		}
	}
}
