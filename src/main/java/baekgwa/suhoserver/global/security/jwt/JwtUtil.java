package baekgwa.suhoserver.global.security.jwt;

import static baekgwa.suhoserver.global.security.constant.JwtConstant.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import baekgwa.suhoserver.global.environment.JwtProperties;
import baekgwa.suhoserver.model.user.entity.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.global.util
 * FileName    : JwtUtil
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
public class JwtUtil {

	private final JwtProperties jwtProperties;
	private SecretKey secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
	}

	public boolean isExpired(String token) {
		try {
			Date expiration = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration();
			return expiration.before(new Date());
		} catch (Exception e) {
			return true;
		}
	}

	public String createJwt(Long userId, UserRole role) {
		return Jwts.builder()
			.issuedAt(Date.from(Instant.now()))
			.expiration(Date.from(Instant.now().plusSeconds(jwtProperties.getTokenExpirationMin() * 60)))
			.claim(CLAIM_KEY_USER_ROLE, role.name())
			.claim(CLAIM_KEY_USER_ID, userId)
			.signWith(secretKey)
			.compact();
	}

	public String getUserRole(String token) {
		try {
			return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
				.get(CLAIM_KEY_USER_ROLE, String.class);
		} catch (Exception e) {
			return null;
		}
	}

	public Long getUserId(String token) {
		try {
			return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
				.get(CLAIM_KEY_USER_ID, Long.class);
		} catch (Exception e) {
			return null;
		}
	}
}
