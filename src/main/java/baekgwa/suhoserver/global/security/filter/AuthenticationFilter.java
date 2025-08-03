package baekgwa.suhoserver.global.security.filter;

import static baekgwa.suhoserver.global.security.constant.JwtConstant.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.global.response.ResponseUtil;
import baekgwa.suhoserver.global.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.global.filter
 * FileName    : AuthenticationFilter
 * Author      : Baekgwa
 * Date        : 2025-08-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-02     Baekgwa               Initial creation
 */
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 1. Token cookie 에서 추출
		Cookie[] cookies = request.getCookies();
		if(cookies == null) {
			filterChain.doFilter(request, response);
			return;
		}
		String accessToken = Arrays.stream(cookies)
			.filter(cookie -> cookie.getName().equals("accessToken"))
			.map(Cookie::getValue)
			.findFirst()
			.orElse(null);

		// 2. token 유무 검증
		if (accessToken == null) {
			filterChain.doFilter(request, response);
			return;
		}

		// 3. token 유효성 검증
		if (jwtUtil.isExpired(accessToken)) {
			// expired 되었다면, 로그인 만료 응답 진행
			ResponseUtil.errorResponse(response, ErrorCode.EXPIRED_INVALID_TOKEN, objectMapper);
			// 혹시 모를 쿠키 token 쿠키 삭제 처리
			ResponseUtil.removeCookie(response, ACCESS_TOKEN_COOKIE_NAME);
			return;
		}

		// 4. token 에서 정보 추출
		String userRole = "ROLE_" + jwtUtil.getUserRole(accessToken);
		Long userId = jwtUtil.getUserId(accessToken);

		// 5. Security Context 에 인증 정보 추가
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole);
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userId, null, List.of(authority));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// 6. 완료 후, 다음 filter 로 이동
		filterChain.doFilter(request, response);
	}
}
