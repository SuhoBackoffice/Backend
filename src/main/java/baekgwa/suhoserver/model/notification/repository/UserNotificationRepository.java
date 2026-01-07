package baekgwa.suhoserver.model.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.notification.entity.UserNotificationEntity;

/**
 * PackageName : baekgwa.suhoserver.model.notification.repository
 * FileName    : UserNotificationRepository
 * Author      : Baekgwa
 * Date        : 26. 1. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 7.     Baekgwa               Initial creation
 */
public interface UserNotificationRepository extends JpaRepository<UserNotificationEntity, Long> {

	@EntityGraph(attributePaths = "notification")
	List<UserNotificationEntity> findAllByUserIdAndIsReadFalse(Long userId);
}
