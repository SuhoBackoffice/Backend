package baekgwa.suhoserver.global.response;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.global.response
 * FileName    : ResponseUtil
 * Author      : Baekgwa
 * Date        : 2025-08-03
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-03     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseUtil {

	private static final String COOKIE_DOMAIN = ".suhotech.co.kr";

	public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds, boolean isProd) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(true);

		if (isProd) {
			cookie.setDomain(COOKIE_DOMAIN);
			cookie.setSecure(true);
		}

		cookie.setPath("/");
		cookie.setMaxAge(maxAgeSeconds);
		response.addCookie(cookie);
	}

	public static void removeCookie(HttpServletResponse response, String name, boolean isProd) {
		Cookie cookie = new Cookie(name, null);
		cookie.setHttpOnly(true);

		if (isProd) {
			cookie.setDomain(COOKIE_DOMAIN);
			cookie.setSecure(true);
		}

		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	public static void errorResponse(HttpServletResponse response, ErrorCode errorCode,
		ObjectMapper objectMapper) throws IOException {
		BaseResponse<Void> errorResponse = BaseResponse.fail(errorCode);
		response.setContentType("application/json");
		response.setStatus(errorCode.getStatus().value());
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
