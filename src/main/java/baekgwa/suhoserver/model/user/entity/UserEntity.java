package baekgwa.suhoserver.model.user.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.user.entity
 * FileName    : UserEntity
 * Author      : Baekgwa
 * Date        : 2025-08-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-02     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends TemporalEntity {

	@Id @Tsid
	@Column(name = "id")
	private Long id;

	@Column(name = "login_id", unique = true)
	private String loginId;

	@Column(name = "password")
	private String password;

	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private UserRole role;

	public static UserEntity createNewUser(String loginId, String password, String username, UserRole role) {
		return new UserEntity(loginId, password, username, role);
	}

	/**
	 * Entity 내부 생성자
	 * @param loginId
	 * @param password
	 * @param username
	 * @param role
	 */
	@Builder(access = AccessLevel.PRIVATE)
	private UserEntity(String loginId, String password, String username, UserRole role) {
		this.loginId = loginId;
		this.password = password;
		this.username = username;
		this.role = role;
	}
}
