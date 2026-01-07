package baekgwa.suhoserver.model.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.notification.entity.NotificationEntity;

/**
 * PackageName : baekgwa.suhoserver.model.notification.repository
 * FileName    : NotificationRepository
 * Author      : Baekgwa
 * Date        : 26. 1. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 7.     Baekgwa               Initial creation
 */
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
