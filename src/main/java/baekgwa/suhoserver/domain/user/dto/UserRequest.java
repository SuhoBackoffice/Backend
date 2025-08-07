package baekgwa.suhoserver.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.user.dto
 * FileName    : UserRequest
 * Author      : Baekgwa
 * Date        : 2025-08-03
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-03     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequest {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class SignupDto {
		@NotBlank(message = "로그인 아이디는 필수 입니다.")
		private String loginId;
		@NotBlank(message = "비밀번호는 필수 입니다.")
		private String password;
		@NotBlank(message = "이름은 필수 입니다.")
		private String username;
	}
}
