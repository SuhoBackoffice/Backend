package baekgwa.suhoserver.domain.authentication.controller;

import static baekgwa.suhoserver.global.security.constant.JwtConstant.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.suhoserver.domain.authentication.dto.AuthRequest;
import baekgwa.suhoserver.domain.authentication.dto.AuthResponse;
import baekgwa.suhoserver.domain.authentication.service.AuthService;
import baekgwa.suhoserver.global.environment.JwtProperties;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.ResponseUtil;
import baekgwa.suhoserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.authentication.controller
 * FileName    : AuthController
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
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "회원 인증")
public class AuthController {

	private final AuthService authService;
	private final JwtProperties jwtProperties;

	@PostMapping("/login")
	@Operation(summary = "로그인")
	public BaseResponse<AuthResponse.LoginResponse> login(
		@Valid @RequestBody AuthRequest.LoginDto request,
		HttpServletResponse response
	) {
		AuthResponse.LoginDto loginDto = authService.login(request);
		ResponseUtil.addCookie(
			response,
			ACCESS_TOKEN_COOKIE_NAME,
			loginDto.getAccessToken(),
			jwtProperties.getTokenExpirationMin().intValue() * 60);

		ResponseUtil.addCookie(
			response,
			COOKIE_USER_ROLE,
			loginDto.getLoginResponse().getRole(),
			jwtProperties.getTokenExpirationMin().intValue() * 60);

		return BaseResponse.success(SuccessCode.LOGIN_SUCCESS, loginDto.getLoginResponse());
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃")
	public BaseResponse<Void> logout(HttpServletResponse response) {
		ResponseUtil.removeCookie(response, ACCESS_TOKEN_COOKIE_NAME);
		return BaseResponse.success(SuccessCode.LOGOUT_SUCCESS);
	}

	@GetMapping("/admin")
	@Operation(summary = "admin 접근 확인 용")
	public BaseResponse<Void> isAdmin() {
		return BaseResponse.success(SuccessCode.REQUEST_SUCCESS);
	}

	@GetMapping("/staff")
	@Operation(summary = "staff 접근 확인 용")
	public BaseResponse<Void> isStaff() {
		return BaseResponse.success(SuccessCode.REQUEST_SUCCESS);
	}

	@GetMapping("/login")
	@Operation(summary = "로그인 유무 확인")
	public BaseResponse<Void> isLogin() {
		return BaseResponse.success(SuccessCode.REQUEST_SUCCESS);
	}
}
