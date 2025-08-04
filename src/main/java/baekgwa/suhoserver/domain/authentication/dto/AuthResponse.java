package baekgwa.suhoserver.domain.authentication.dto;

import lombok.AccessLevel;
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
	public static class LoginResponse {
		private final String accessToken;

		private LoginResponse(String accessToken) {
			this.accessToken = accessToken;
		}

		public static LoginResponse from(String accessToken) {
			return new LoginResponse(accessToken);
		}
	}
}
