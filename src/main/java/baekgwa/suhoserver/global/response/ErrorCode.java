package baekgwa.suhoserver.global.response;

import static org.springframework.http.HttpStatus.*;

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
	INVALID_LOGIN_INFO(BAD_REQUEST, "1000", "잘못된 로그인 정보입니다."),
	EXPIRED_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "1001", "로그인 정보가 만료되었습니다. 다시 로그인 해주세요."),
	NEED_LOGIN(HttpStatus.UNAUTHORIZED, "1002", "로그인이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "1003", "시스템 권한이 부족합니다."),

	//User : 2000 ~ 2999
	DUPLICATE_LOGIN_ID(BAD_REQUEST, "2000", "중복된 로그인 아이디 입니다."),
	INVALID_USER_ID(BAD_REQUEST, "2001", "탈퇴한 회원이거나, 잘못된 로그인 정보 입니다. 재로그인 해주세요."),

	//Version : 3000 ~ 3999
	DUPLICATE_VERSION_NAME(BAD_REQUEST, "3000", "해당 버전이 이미 있습니다."),
	NOT_FOUND_VERSION(BAD_REQUEST, "3001", "해당 버전이 존재하지 않습니다."),

	// Branch : 4000 ~ 4999
	NOT_FOUND_BRANCH_BOM(BAD_REQUEST, "4000", "저장된 분기레일 BOM 정보가 없습니다. 업데이트 해주세요."),
	ALREADY_UPLOADED_COMPLETE_BRANCH_BOM(BAD_REQUEST, "4001", "오늘 해당 분기레일 정보를 이미 업데이트 하였습니다. 관리자에게 문의 해주세요"),
	NOT_FOUND_BRANCH_TYPE(BAD_REQUEST, "4002", "해당 분기레일 정보를 찾을 수 없습니다."),

	// Project : 5000 ~ 5999
	PROJECT_END_AFTER_START_ERROR(BAD_REQUEST, "5000", "종료일이 시작일보다 빠를수 없습니다."),
	NOT_FOUND_PROJECT(BAD_REQUEST, "5001", "해당 프로젝트를 찾을 수 없습니다."),
	INVALID_VERSION_BRANCH(BAD_REQUEST, "5002", "프로젝트 버전과, 해당 분기레일의 버전이 일치하지 않습니다."),
	ALREADY_EXIST_PROJECT_STRAIGHT_DATA(BAD_REQUEST, "5003",
		"이미 동일한 길이와 버전이 해당 프로젝트에 등록되어 있습니다. 변경이 필요하다면 수정/삭제 기능을 사용해 주세요."),
	INVALID_PROJECT_STRAIGHT_REGISTER_DATA_DUPLICATION(BAD_REQUEST, "5004",
		"입력한 [길이, 타입]이 동일한 레일이 있습니다. 확인해주세요. EX) 2400 A 타입 1개, 2400 A 타입 2개 추가 등록 요청"),
	NOT_EXIST_PROJECT_STRAIGHT(BAD_REQUEST, "5005", "삭제되었거나, 없는 직선 레일 입니다. 확인해주세요."),
	NOT_EXIST_PROJECT_BRANCH(BAD_REQUEST, "5006", "삭제되었거나, 없는 분기 레일 입니다. 확인해주세요."),
	ALREADY_EXIST_PROJECT_BRANCH_DATA(BAD_REQUEST, "5007",
		"이미 동일한 분기레일이 존재합니다. 변경이 필요하다면 수정/삭제 기능을 사용해 주세요."),
	CREATE_QUANTITY_LIST_BRANCH_BOM_VALID_FAIL(BAD_REQUEST, "5008", "프로젝트 BOM List 생성 실패. 관리자에게 문의해 주세요."),
	PATCH_BRANCH_COUNT_FAIL_DIFF_ZERO(BAD_REQUEST, "5009", "수량 업데이트 취소. 이전 수량과 동일합니다."),
	PATCH_STRAIGHT_COUNT_FAIL_DIFF_ZERO(BAD_REQUEST, "5010", "수량 업데이트 취소. 이전 수량과 동일합니다."),

	// Straight : 6000 ~ 6999
	DUPLICATE_STRAIGHT_TYPE(BAD_REQUEST, "6000", "이미 존재하는 직선레일 타입 입니다."),
	NOT_FOUND_STRAIGHT_TYPE(BAD_REQUEST, "6001", "레일 타입 정보를 찾을 수 없습니다."),
	INVALID_LOOP_RAIL_TYPE_DATA(BAD_REQUEST, "6002", "루프 레일에 일반 레일 타입이 들어가있습니다."),
	NOT_MATCH_STRAIGHT_LOOP_TYPE(BAD_REQUEST, "6003", "일부 데이터가 일반 레일인데, 루프 레일 타입으로 지정되었습니다."),
	NOT_MATCH_STRAIGHT_NORMAL_TYPE(BAD_REQUEST, "6003", "일부 데이터가 루프 레일인데, 일반 레일 타입으로 지정되었습니다."),
	NOT_EXIST_STRAIGHT_TYPE(BAD_REQUEST, "6004", "존재하지 않는 직선레일 타입 입니다."),
	INVALID_STRAIGHT_RAIL_LENGTH(BAD_REQUEST, "6005", "유효하지 않은 직선레일 길이입니다. 3600 이하만 허용됩니다."),

	// Material : 7000 ~ 7999
	INVALID_MATERIAL_KEYWORD_OVER_2(BAD_REQUEST, "7000", "키워드는 2글자 이상이어야 합니다."),
	NOT_EXIST_MATERIAL(BAD_REQUEST, "7001", "등록되지 않은 제품입니다."),

	// Work : 8000 ~ 8999
	ALREADY_EXIST_DAILY_REPORT(BAD_REQUEST, "8000", "이미 해당일에 업무 보고를 진행하였습니다."),
	NOT_FOUND_WORK_REPORT(BAD_REQUEST, "8001", "보고서를 찾을 수 없습니다."),
	NOT_MATCH_STRAIGHT_PRODUCTION_SERIAL_COUNT(BAD_REQUEST, "8002", "선택한 직선레일 수량이 생산 수량과 일치하지 않습니다."),
	NOT_REGISTERED_PROJECT_STRAIGHT(BAD_REQUEST, "8003", "해당 프로젝트에 없는 직선레일 입니다."),
	INVALID_STRAIGHT_SERIAL(BAD_REQUEST, "8004", "선택할 수 없는 직선레일 시리얼이 포함되어 있습니다."),
	INACTIVE_STRAIGHT_SERIAL(BAD_REQUEST, "8005", "비활성화된 직선레일 시리얼이 포함되어 있습니다."),
	ALREADY_PRODUCED_STRAIGHT_SERIAL(BAD_REQUEST, "8006", "이미 생산 완료된 직선레일 시리얼이 포함되어 있습니다."),
	ALREADY_USED_STRAIGHT_SERIAL(BAD_REQUEST, "8007", "이미 작업 보고에 사용된 직선레일 시리얼이 포함되어 있습니다."),
	NOT_REGISTERED_PROJECT_BRANCH(BAD_REQUEST, "8008", "해당 프로젝트에 없는 분기레일 입니다."),
	NOT_MATCH_BRANCH_PRODUCTION_SERIAL_COUNT(BAD_REQUEST, "8009", "선택한 분기레일 수량이 생산 수량과 일치하지 않습니다."),
	INVALID_BRANCH_SERIAL(BAD_REQUEST, "8010", "선택할 수 없는 분기레일 시리얼이 포함되어 있습니다."),
	INACTIVE_BRANCH_SERIAL(BAD_REQUEST, "8011", "비활성화된 분기레일 시리얼이 포함되어 있습니다."),
	ALREADY_PRODUCED_BRANCH_SERIAL(BAD_REQUEST, "8012", "이미 생산 완료된 분기레일 시리얼이 포함되어 있습니다."),
	ALREADY_USED_BRANCH_SERIAL(BAD_REQUEST, "8013", "이미 작업 보고에 사용된 분기레일 시리얼이 포함되어 있습니다."),
	REPORT_APPROVED_BUT_REJECT_REASON_EXIST(BAD_REQUEST, "8014", "작업 보고는 승인이나, 반려 사유가 포함되어 있습니다."),
	REPORT_REJECTED_BUT_REJECT_REASON_NOT_EXIST(BAD_REQUEST, "8015", "작업 보고 반려 사유가 없습니다."),
	ALREADY_UPDATED_REPORT(BAD_REQUEST, "8016", "이미 반려 혹은 승인된 작업 보고서 입니다."),
	REPORT_PROJECT_STRAIGHT_NOT_FOUND(BAD_REQUEST, "8017", "보고서에 작성된 직선레일은 더 이상 존재하지 않습니다."),
	REPORT_UPDATE_FAIL_PRODUCTION_QUANTITY_EXCEEDED(BAD_REQUEST, "8018", "총 수량보다 제작 수량이 많아 보고서가 승인되지 않습니다."),
	REPORT_UPDATE_FAIL_PENDING_IMPOSSIBLE(BAD_REQUEST, "8019", "보고서는 승인 혹은 반려만 가능합니다."),
	REPORT_PROJECT_BRANCH_NOT_FOUND(BAD_REQUEST, "8020", "보고서에 작성된 분기 레일은 더 이상 존재하지 않습니다."),

	//Notification: 9000 ~ 9499
	NOT_FOUNT_NOTIFICATION(NOT_FOUND, "9000", "존재하지 않는 알림 입니다."),

	//Common: 9500 ~ 9999
	NOT_FOUND_URL(NOT_FOUND, "9500", "요청하신 URL 을 찾을 수 없습니다."),
	NOT_SUPPORTED_METHOD(METHOD_NOT_ALLOWED, "9501", "요청 메서드를 찾을 수 없습니다."),
	VALIDATION_FAIL_ERROR(BAD_REQUEST, "9502", ""),
	INVALID_INPUT_VALUE(BAD_REQUEST, "9503", "올바르지 않은 입력값입니다."),
	HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "9504", "요청이 거부되었습니다."),
	METHOD_ARGUMENT_TYPE_MISS_MATCH(BAD_REQUEST, "9505", "요청 파라미터 타입 불일치. API 문서 확인해주세요."),
	INVALID_PAGINATION_PARAMETER(BAD_REQUEST, "9506", "올바르지 않은 페이지 네이션 파라미터 요청입니다."),
	INVALID_EXCEL_PARSE_ERROR(BAD_REQUEST, "9507", "올바르지 않은 Excel 데이터입니다. 관리자에게 문의해 주세요."),
	UPLOAD_FILE_FAIL(BAD_REQUEST, "9508", "파일 업로드 실패."),
	DELETE_FILE_FAIL(BAD_REQUEST, "9509", "파일 삭제 실패."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9999", "서버 내부 오류 발생했습니다");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
