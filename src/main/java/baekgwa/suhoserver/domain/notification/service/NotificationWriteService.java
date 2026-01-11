package baekgwa.suhoserver.domain.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.infra.notification.event.NotificationEvent;
import baekgwa.suhoserver.model.notification.entity.NotificationEntity;
import baekgwa.suhoserver.model.notification.entity.UserNotificationEntity;
import baekgwa.suhoserver.model.notification.repository.NotificationRepository;
import baekgwa.suhoserver.model.notification.repository.UserNotificationRepository;
import baekgwa.suhoserver.model.user.entity.UserEntity;
import baekgwa.suhoserver.model.user.entity.UserRole;
import baekgwa.suhoserver.model.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.notification.service
 * FileName    : NotificationWriteService
 * Author      : Baekgwa
 * Date        : 26. 1. 10.
 * Description : 알림 데이터를 생성하고 저장하는 비즈니스 로직 처리
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 10.     Baekgwa               Initial creation
 */
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationWriteService {

	private final NotificationRepository notificationRepository;
	private final UserNotificationRepository userNotificationRepository;
	private final UserRepository userRepository;

	/**
	 * 특정 역할을 가진 모든 유저에게 알림 데이터를 생성하고 저장합니다.
	 * @param event 처리할 이벤트
	 * @param targetRole 알림 받을 회원 권한 목록
	 * @return
	 */
	public List<UserNotificationEntity> createBulkNotification(NotificationEvent event, UserRole targetRole) {
		NotificationEntity notification = NotificationEntity.of(
			event.content(),
			event.url(),
			event.type()
		);
		notificationRepository.save(notification);

		List<UserEntity> targets = userRepository.findAllByRole(targetRole);

		List<UserNotificationEntity> userNotifications = targets.stream()
			.map(user -> UserNotificationEntity.createNewNotification(user, notification))
			.toList();

		return userNotificationRepository.saveAll(userNotifications);
	}

	/**
	 * 알림 읽기 처리
	 * @param userNotificationId
	 */
	public void markAsRead(Long userNotificationId) {
		UserNotificationEntity userNotification = userNotificationRepository.findById(userNotificationId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUNT_NOTIFICATION));
		userNotification.markAsRead();
	}
}
