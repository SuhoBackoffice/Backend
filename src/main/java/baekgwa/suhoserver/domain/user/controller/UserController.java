package baekgwa.suhoserver.domain.user.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.suhoserver.domain.user.dto.UserRequest;
import baekgwa.suhoserver.domain.user.dto.UserResponse;
import baekgwa.suhoserver.domain.user.service.UserService;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.user.controller
 * FileName    : UserController
 * Author      : Baekgwa
 * Date        : 2025-08-03
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-03     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User Controller", description = "회원 도메인")
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	@Operation(summary = "회원 가입")
	public BaseResponse<Void> login(
		@Valid @RequestBody UserRequest.SignupDto signupDto
	) {
		userService.signup(signupDto);
		return BaseResponse.success(SuccessCode.SIGNUP_SUCCESS);
	}

	@GetMapping("/info")
	@Operation(summary = "로그인 정보 조회")
	public BaseResponse<UserResponse.UserInfoDto> getUserinfo(
		Principal principal
	) {
		UserResponse.UserInfoDto userInfoDto = userService.getUserInfo(Long.parseLong(principal.getName()));
		return BaseResponse.success(SuccessCode.REQUEST_SUCCESS, userInfoDto);
	}
}
