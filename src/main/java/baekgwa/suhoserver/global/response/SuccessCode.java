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
	CREATE_NEW_BRANCH_BOM_SUCCESS(HttpStatus.CREATED, "신규 분기 BOM 리스트 생성 성공"),

	// Project
	CREATE_NEW_PROJECT_SUCCESS(HttpStatus.CREATED, "신규 프로젝트 생성 완료"),
	REGISTER_PROJECT_BRANCH_SUCCESS(HttpStatus.CREATED, "프로젝트 분기 정보 등록 완료"),
	GET_PROJECT_INFORMATION_SUCCESS(HttpStatus.CREATED, "프로젝트 정보 조회 성공"),

	// Common
	REQUEST_SUCCESS(HttpStatus.OK, "요청 응답 성공.");

	private final HttpStatus status;
	private final String message;
}
