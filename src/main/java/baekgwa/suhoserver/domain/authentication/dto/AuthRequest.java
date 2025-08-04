package baekgwa.suhoserver.domain.authentication.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.authentication.dto
 * FileName    : AuthRequest
 * Author      : Baekgwa
 * Date        : 2025-08-03
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-03     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthRequest {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class LoginDto {
		private String loginId;
		private String password;

		@Builder(access = AccessLevel.PRIVATE)
		private LoginDto(String loginId, String password) {
			this.loginId = loginId;
			this.password = password;
		}
	}
}
