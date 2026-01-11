package baekgwa.suhoserver.model.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.user.entity.UserEntity;
import baekgwa.suhoserver.model.user.entity.UserRole;

/**
 * PackageName : baekgwa.suhoserver.model.user.repository
 * FileName    : UserRepository
 * Author      : Baekgwa
 * Date        : 2025-08-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-02     Baekgwa               Initial creation
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByLoginId(String loginId);

	List<UserEntity> findAllByRole(UserRole userRole);
}
