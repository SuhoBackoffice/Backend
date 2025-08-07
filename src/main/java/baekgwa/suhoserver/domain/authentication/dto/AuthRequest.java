package baekgwa.suhoserver.domain.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
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
		@NotBlank(message = "로그인 아이디는 필수 입니다.")
		private String loginId;
		@NotBlank(message = "비밀번호는 필수 입니다.")
		private String password;
	}
}
