package baekgwa.suhoserver.global.response;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.global.response
 * FileName    : ErrorCode
 * Author      : Baekgwa
 * Date        : 2025-08-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-02     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

	//Auth : 1000 ~ 1999
	INVALID_LOGIN_INFO(HttpStatus.BAD_REQUEST, "1000", "잘못된 로그인 정보입니다."),
	EXPIRED_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "1001", "로그인 정보가 만료되었습니다. 다시 로그인 해주세요."),
	NEED_LOGIN(HttpStatus.UNAUTHORIZED, "1002", "로그인이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "1003", "시스템 권한이 부족합니다."),

	//User : 2000 ~ 2999
	DUPLICATE_LOGIN_ID(HttpStatus.BAD_REQUEST, "2000", "중복된 로그인 아이디 입니다."),
	INVALID_USER_ID(HttpStatus.BAD_REQUEST, "2001", "탈퇴한 회원이거나, 잘못된 로그인 정보 입니다. 재로그인 해주세요."),

	//Common: 9000 ~ 9999
	NOT_FOUND_URL(HttpStatus.NOT_FOUND, "9001", "요청하신 URL 을 찾을 수 없습니다."),
	NOT_SUPPORTED_METHOD(HttpStatus.METHOD_NOT_ALLOWED, "9002", "요청 메서드를 찾을 수 없습니다."),
	VALIDATION_FAIL_ERROR(HttpStatus.BAD_REQUEST, "9003", ""),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "9004", "올바르지 않은 입력값입니다."),
	HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "9007", "요청이 거부되었습니다."),
	METHOD_ARGUMENT_TYPE_MISS_MATCH(HttpStatus.BAD_REQUEST, "9008", "요청 파라미터 타입 불일치. API 문서 확인해주세요."),
	INVALID_PAGINATION_PARAMETER(HttpStatus.BAD_REQUEST, "9009", "올바르지 않은 페이지 네이션 파라미터 요청입니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9999", "서버 내부 오류 발생했습니다");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
