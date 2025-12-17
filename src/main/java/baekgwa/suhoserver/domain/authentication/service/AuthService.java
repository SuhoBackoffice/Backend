package baekgwa.suhoserver.domain.authentication.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.authentication.dto.AuthRequest;
import baekgwa.suhoserver.domain.authentication.dto.AuthResponse;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.global.security.jwt.JwtUtil;
import baekgwa.suhoserver.model.user.entity.UserEntity;
import baekgwa.suhoserver.model.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.authentication.service
 * FileName    : AuthService
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
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Transactional(readOnly = true)
	public AuthResponse.LoginDto login(AuthRequest.LoginDto loginDto) {

		// 1. 사용자 정보 조회
		UserEntity findUser = userRepository.findByLoginId(loginDto.getLoginId())
			.orElseThrow(() -> new GlobalException(ErrorCode.INVALID_LOGIN_INFO));

		// 2. 패스워드 검증
		if (!passwordEncoder.matches(loginDto.getPassword(), findUser.getPassword())) {
			throw new GlobalException(ErrorCode.INVALID_LOGIN_INFO);
		}

		// 3. 회원 토큰 생성
		String accessToken = jwtUtil.createJwt(findUser.getId(), findUser.getRole());

		// 4. 반환
		AuthResponse.LoginResponse loginResponse = AuthResponse.LoginResponse.of(findUser);
		return AuthResponse.LoginDto.from(accessToken, loginResponse);
	}
}
