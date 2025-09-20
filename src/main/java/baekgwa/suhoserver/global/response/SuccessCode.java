package baekgwa.suhoserver.global.response;

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
	LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공"),
	LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공"),

	// User
	SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입 성공"),
	GET_USER_INFO_SUCCESS(HttpStatus.OK, "유저 정보 조회 성공"),
	
	// Version
	CREATE_NEW_VERSION_SUCCESS(HttpStatus.CREATED, "신규 버전 생성 성공"),
	GET_ALL_VERSION_LIST_SUCCESS(HttpStatus.OK, "버전 리스트 조회 성공"),

	// Branch
	CREATE_NEW_BRANCH_BOM_SUCCESS(HttpStatus.CREATED, "분기 BOM 리스트 추가 성공"),
	GET_LATEST_BRANCH_BOM_SUCCESS(HttpStatus.OK, "해당 버전 최신 분기 레일 조회 성공"),
	GET_BRANCH_BOM_SUCCESS(HttpStatus.OK, "분기레일 BOM 리스트 조회 성공"),


	// Straight
	CREATE_NEW_STRAIGHT_TYPE_SUCCESS(HttpStatus.CREATED, "신규 직선레일 타입 추가 완료"),
	GET_ALL_STRAIGHT_TYPE_LIST_SUCCESS(HttpStatus.OK, "직선 레일 타입 조회 성공"),
	GET_ALL_LOOP_STRAIGHT_TYPE_LIST_SUCCESS(HttpStatus.OK, "루프/직선 레일 타입 조회 성공"),

	// Project
	CREATE_NEW_PROJECT_SUCCESS(HttpStatus.CREATED, "신규 프로젝트 생성 완료"),
	REGISTER_PROJECT_BRANCH_SUCCESS(HttpStatus.CREATED, "프로젝트 분기 정보 등록 완료"),
	REGISTER_PROJECT_NORMAL_STRAIGHT_SUCCESS(HttpStatus.CREATED, "프로젝트 직선레일 정보 등록 완료"),
	GET_PROJECT_DETAIL_INFORMATION_SUCCESS(HttpStatus.OK, "프로젝트 상세 정보 조회 성공"),
	GET_PROJECT_INFORMATION_SUCCESS(HttpStatus.OK, "프로젝트 정보 조회 성공"),
	GET_PROJECT_SEARCH_SORT_SUCCESS(HttpStatus.OK, "프로젝트 정렬 조건 조회/패칭 성공"),
	GET_PROJECT_DETAIL_BRANCH_INFO_SUCCESS(HttpStatus.OK, "프로젝트 분기레일 상세 정보 조회 성공"),
	GET_PROJECT_DETAIL_STRAIGHT_INFO_SUCCESS(HttpStatus.OK, "프로젝트 분기레일 상세 정보 조회 성공"),
	DELETE_PROJECT_STRAIGHT_SUCCESS(HttpStatus.OK, "프로젝트 직선레일 삭제 완료"),
	PATCH_PROJECT_STRAIGHT_SUCCESS(HttpStatus.OK, "프로젝트 직선레일 수정 완료"),
	DELETE_PROJECT_BRANCH_SUCCESS(HttpStatus.OK, "프로젝트 분기레일 삭제 완료"),
	PATCH_PROJECT_BRANCH_SUCCESS(HttpStatus.OK, "프로젝트 분기레일 수정 완료"),

	// File Uploader
	UPLOAD_FILE_SUCCESS(HttpStatus.CREATED, "파일 업로드 완료"),
	DELETE_FILE_SUCCESS(HttpStatus.OK, "파일 삭제 완료"),

	// Material & Bom
	GET_MATERIAL_FIND_LIST_SUCCESS(HttpStatus.OK, "자재 목록 조회 성공"),
	POST_MATERIAL_INBOUND_UPDATE_SUCCESS(HttpStatus.CREATED, "자재 입고 등록 완료"),

	// Common
	REQUEST_SUCCESS(HttpStatus.OK, "요청 응답 성공.");

	private final HttpStatus status;
	private final String message;
}
