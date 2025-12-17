package baekgwa.suhoserver.domain.authentication.dto;

import baekgwa.suhoserver.model.user.entity.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.authentication.dto
 * FileName    : AuthResponse
 * Author      : Baekgwa
 * Date        : 2025-08-03
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-03     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthResponse {

	@Getter
	public static class LoginDto {
		private final String accessToken;

		private LoginDto(String accessToken) {
			this.accessToken = accessToken;
		}

		public static LoginDto from(String accessToken) {
			return new LoginDto(accessToken);
		}
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class LoginResponse {
		private final String username;
		private final String role;

		public static LoginResponse of(UserEntity user) {
			return new LoginResponse(user.getUsername(), user.getRole().name());
		}
	}
}
