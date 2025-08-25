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

	//Version : 3000 ~ 3999
	DUPLICATE_VERSION_NAME(HttpStatus.BAD_REQUEST, "3000", "해당 버전이 이미 있습니다."),
	NOT_FOUND_VERSION(HttpStatus.BAD_REQUEST, "3001", "해당 버전이 존재하지 않습니다."),

	// Branch : 4000 ~ 4999
	NOT_FOUND_BRANCH_BOM(HttpStatus.BAD_REQUEST, "4000", "저장된 분기레일 BOM 정보가 없습니다. 업데이트 해주세요."),
	ALREADY_UPLOADED_COMPLETE_BRANCH_BOM(HttpStatus.BAD_REQUEST, "4001", "오늘 해당 분기레일 정보를 이미 업데이트 하였습니다. 관리자에게 문의 해주세요"),
	NOT_FOUND_BRANCH_TYPE(HttpStatus.BAD_REQUEST, "4002", "해당 분기레일 정보를 찾을 수 없습니다."),

	// Project : 5000 ~ 5999
	PROJECT_END_AFTER_START_ERROR(HttpStatus.BAD_REQUEST, "5000", "종료일이 시작일보다 빠를수 없습니다."),
	NOT_FOUND_PROJECT(HttpStatus.BAD_REQUEST, "5001", "해당 프로젝트를 찾을 수 없습니다."),
	INVALID_VERSION_BRANCH(HttpStatus.BAD_REQUEST, "5002", "프로젝트 버전과, 해당 분기레일의 버전이 일치하지 않습니다."),
	ALREADY_EXIST_PROJECT_STRAIGHT_DATA(HttpStatus.BAD_REQUEST, "5003",
		"이미 동일한 길이와 버전이 해당 프로젝트에 등록되어 있습니다. 변경이 필요하다면 수정/삭제 기능을 사용해 주세요."),
	INVALID_PROJECT_STRAIGHT_REGISTER_DATA_DUPLICATION(HttpStatus.BAD_REQUEST, "5004", "입력한 [길이, 타입]이 동일한 레일이 있습니다. 확인해주세요. EX) 2400 A 타입 1개, 2400 A 타입 2개 추가 등록 요청"),
	NOT_EXIST_PROJECT_STRAIGHT(HttpStatus.BAD_REQUEST, "5005", "삭제되었거나, 없는 레일 입니다. 확인해주세요."),

	// Straight : 6000 ~ 69999
	DUPLICATE_STRAIGHT_TYPE(HttpStatus.BAD_REQUEST, "6000", "이미 존재하는 직선레일 타입 입니다."),
	NOT_FOUND_STRAIGHT_TYPE(HttpStatus.BAD_REQUEST, "6001", "레일 타입 정보를 찾을 수 없습니다."),
	INVALID_LOOP_RAIL_TYPE_DATA(HttpStatus.BAD_REQUEST, "6002", "루프 레일에 일반 레일 타입이 들어가있습니다."),
	NOT_MATCH_STRAIGHT_LOOP_TYPE(HttpStatus.BAD_REQUEST, "6003", "일부 데이터가 일반 레일인데, 루프 레일 타입으로 지정되었습니다."),
	NOT_MATCH_STRAIGHT_NORMAL_TYPE(HttpStatus.BAD_REQUEST, "6003", "일부 데이터가 루프 레일인데, 일반 레일 타입으로 지정되었습니다."),
	NOT_EXIST_STRAIGHT_TYPE(HttpStatus.BAD_REQUEST, "6004", "존재하지 않는 직선레일 타입 입니다."),

	//Common: 9000 ~ 9999
	NOT_FOUND_URL(HttpStatus.NOT_FOUND, "9001", "요청하신 URL 을 찾을 수 없습니다."),
	NOT_SUPPORTED_METHOD(HttpStatus.METHOD_NOT_ALLOWED, "9002", "요청 메서드를 찾을 수 없습니다."),
	VALIDATION_FAIL_ERROR(HttpStatus.BAD_REQUEST, "9003", ""),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "9004", "올바르지 않은 입력값입니다."),
	HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "9007", "요청이 거부되었습니다."),
	METHOD_ARGUMENT_TYPE_MISS_MATCH(HttpStatus.BAD_REQUEST, "9008", "요청 파라미터 타입 불일치. API 문서 확인해주세요."),
	INVALID_PAGINATION_PARAMETER(HttpStatus.BAD_REQUEST, "9009", "올바르지 않은 페이지 네이션 파라미터 요청입니다."),
	INVALID_EXCEL_PARSE_ERROR(HttpStatus.BAD_REQUEST, "9010", "올바르지 않은 Excel 데이터입니다. 관리자에게 문의해 주세요."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9999", "서버 내부 오류 발생했습니다");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
