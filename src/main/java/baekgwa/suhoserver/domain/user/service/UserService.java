package baekgwa.suhoserver.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.user.dto.UserRequest;
import baekgwa.suhoserver.domain.user.dto.UserResponse;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.user.entity.UserEntity;
import baekgwa.suhoserver.model.user.entity.UserRole;
import baekgwa.suhoserver.model.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.user.service
 * FileName    : UserService
 * Author      : Baekgwa
 * Date        : 2025-08-03
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-03     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void signup(UserRequest.SignupDto signupDto) {

		// 1. 중복 검증
		if(userRepository.findByLoginId(signupDto.getLoginId()).isPresent()) {
			throw new GlobalException(ErrorCode.DUPLICATE_LOGIN_ID);
		}

		// 2. 새로운 회원 데이터 생성
		UserEntity newUser = UserEntity.createNewUser(signupDto.getLoginId(),
			passwordEncoder.encode(signupDto.getPassword()), signupDto.getUsername(), UserRole.USER);

		// 3. 데이터 저장
		userRepository.save(newUser);
	}

	public UserResponse.UserInfoDto getUserInfo(Long userId) {

		// 1. 정보 조회
		UserEntity findUser = userRepository.findById(userId).orElseThrow(
			() -> new GlobalException(ErrorCode.INVALID_USER_ID));

		// 2. DTO 변환 및 반환
		return UserResponse.UserInfoDto.of(findUser.getId(), findUser.getUsername(), findUser.getRole());
	}
}
