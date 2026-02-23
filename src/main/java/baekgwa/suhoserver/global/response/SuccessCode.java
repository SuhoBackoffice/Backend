package baekgwa.suhoserver.global.response;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.global.response
 * FileName    : SuccessCode
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
public enum SuccessCode {

	// Authentication
	LOGIN_SUCCESS(OK, "로그인 성공"),
	LOGOUT_SUCCESS(OK, "로그아웃 성공"),

	// User
	SIGNUP_SUCCESS(CREATED, "회원가입 성공"),
	GET_USER_INFO_SUCCESS(OK, "유저 정보 조회 성공"),

	// Version
	CREATE_NEW_VERSION_SUCCESS(CREATED, "신규 버전 생성 성공"),
	GET_ALL_VERSION_LIST_SUCCESS(OK, "버전 리스트 조회 성공"),

	// Branch
	CREATE_NEW_BRANCH_BOM_SUCCESS(CREATED, "분기 BOM 리스트 추가 성공"),
	GET_LATEST_BRANCH_BOM_SUCCESS(OK, "해당 버전 최신 분기 레일 조회 성공"),
	GET_BRANCH_BOM_SUCCESS(OK, "분기 레일 BOM 리스트 조회 성공"),

	// Straight
	CREATE_NEW_STRAIGHT_TYPE_SUCCESS(CREATED, "신규 직선레일 타입 추가 완료"),
	GET_ALL_STRAIGHT_TYPE_LIST_SUCCESS(OK, "직선 레일 타입 조회 성공"),
	GET_ALL_LOOP_STRAIGHT_TYPE_LIST_SUCCESS(OK, "루프/직선 레일 타입 조회 성공"),
	GET_STRAIGHT_BOM_SUCCESS(OK, "직선 레일 BOM 리스트 조회 성공"),

	// Project
	CREATE_NEW_PROJECT_SUCCESS(CREATED, "신규 프로젝트 생성 완료"),
	REGISTER_PROJECT_BRANCH_SUCCESS(CREATED, "프로젝트 분기 정보 등록 완료"),
	REGISTER_PROJECT_NORMAL_STRAIGHT_SUCCESS(CREATED, "프로젝트 직선레일 정보 등록 완료"),
	GET_PROJECT_DETAIL_INFORMATION_SUCCESS(OK, "프로젝트 상세 정보 조회 성공"),
	GET_PROJECT_INFORMATION_SUCCESS(OK, "프로젝트 정보 조회 성공"),
	GET_PROJECT_SEARCH_SORT_SUCCESS(OK, "프로젝트 정렬 조건 조회/패칭 성공"),
	GET_PROJECT_DETAIL_BRANCH_INFO_SUCCESS(OK, "프로젝트 분기 레일 정보 조회 성공"),
	GET_PROJECT_DETAIL_BRANCH_DETAIL_INFO_SUCCESS(OK, "프로젝트 분기 레일 상세 정보 조회 성공"),
	GET_PROJECT_DETAIL_STRAIGHT_INFO_SUCCESS(OK, "프로젝트 직선 레일 정보 조회 성공"),
	GET_PROJECT_DETAIL_STRAIGHT_DETAIL_INFO_SUCCESS(OK, "프로젝트 직선 레일 상세 정보 조회 성공"),
	DELETE_PROJECT_STRAIGHT_SUCCESS(OK, "프로젝트 직선레일 삭제 완료"),
	PATCH_PROJECT_STRAIGHT_SUCCESS(OK, "프로젝트 직선레일 수정 완료"),
	DELETE_PROJECT_BRANCH_SUCCESS(OK, "프로젝트 분기레일 삭제 완료"),
	PATCH_PROJECT_BRANCH_SUCCESS(OK, "프로젝트 분기레일 수정 완료"),
	GET_PROJECT_BRANCH_CAPACITY(OK, "분기레일 생산 가능 수량 조회 성공"),
	GET_ON_GOING_PROJECT_INFO(OK, "현재 진행중인 프로젝트 목록 조회 성공"),

	// File Uploader
	UPLOAD_FILE_SUCCESS(CREATED, "파일 업로드 완료"),
	DELETE_FILE_SUCCESS(OK, "파일 삭제 완료"),

	// Material & Bom
	GET_MATERIAL_FIND_LIST_SUCCESS(OK, "자재 목록 조회 성공"),
	POST_MATERIAL_INBOUND_UPDATE_SUCCESS(CREATED, "자재 입고 등록 완료"),
	GET_MATERIAL_HISTORY_LIST_SUCCESS(OK, "자재 입고 이력 목록 불러오기 성공"),
	GET_MATERIAL_HISTORY_DETAIL_SUCCESS(OK, "자재 입고 이력 상세 불러오기 성공"),
	GET_MATERIAL_STATE_SUCCESS(OK, "프로젝트 자재 현황 로드 성공"),
	GET_MATERIAL_HISTORY_TYPE_LIST_SUCCESS(OK, "자재 이력 타입 목록 조회 성공"),

	// Report
	POST_WORK_REPORT_SUCCESS(CREATED, "업무 보고가 완료되었습니다. 수고하셨습니다."),
	GET_ABLE_REPORT_STRAIGHT_LIST_SUCCESS(OK, "보고가능 직선레일 목록 조회 성공"),
	GET_ABLE_REPORT_BRANCH_LIST_SUCCESS(OK, "보고가능 분기레일 목록 조회 성공"),
	GET_ABLE_REPORT_STRAIGHT_SERIAL_LIST_SUCCESS(OK, "보고가능 직선레일 시리얼 목록 조회 성공"),
	GET_ABLE_REPORT_BRANCH_SERIAL_LIST_SUCCESS(OK, "보고가능 분기레일 시리얼 목록 조회 성공"),
	GET_WORK_REPORT_DETAIL_SUCCESS(OK, "업무 보고 상세 조회 성공"),
	GET_PROJECT_WORK_REPORT_SUCCESS(OK, "프로젝트 업무 보고 목록 조회 성공"),
	POST_WORK_REPORT_STATUS_SUCCESS(OK, "보고서 상태 변경 완료."),

	// Notification
	PATCH_NOTIFICATION_CHECK_SUCCESS(OK, "알림 메시지 확인 성공."),

	// Common
	REQUEST_SUCCESS(OK, "요청 응답 성공.");

	private final HttpStatus status;
	private final String message;
}
