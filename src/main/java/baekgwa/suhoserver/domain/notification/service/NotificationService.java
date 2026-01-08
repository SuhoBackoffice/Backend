package baekgwa.suhoserver.domain.notification.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import baekgwa.suhoserver.domain.notification.dto.NotificationResponse;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.infra.notification.event.NotificationEvent;
import baekgwa.suhoserver.infra.notification.store.NotificationStore;
import baekgwa.suhoserver.model.notification.entity.NotificationEntity;
import baekgwa.suhoserver.model.notification.entity.UserNotificationEntity;
import baekgwa.suhoserver.model.notification.repository.NotificationRepository;
import baekgwa.suhoserver.model.notification.repository.UserNotificationRepository;
import baekgwa.suhoserver.model.user.entity.UserEntity;
import baekgwa.suhoserver.model.user.entity.UserRole;
import baekgwa.suhoserver.model.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.domain.notification.service
 * FileName    : NotificationService
 * Author      : Baekgwa
 * Date        : 26. 1. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 7.     Baekgwa               Initial creation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final UserNotificationRepository userNotificationRepository;
	private final UserRepository userRepository;
	private final NotificationStore notificationStore;

	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간
	private final NotificationRepository notificationRepository;

	@Transactional(readOnly = true)
	public SseEmitter subscribe(Long userId) {
		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

		emitter.onCompletion(() -> notificationStore.deleteById(userId));
		emitter.onTimeout(() -> notificationStore.deleteById(userId));

		notificationStore.save(userId, emitter);

		sendUnreadNotifications(userId, emitter);

		return emitter;
	}

	private void sendUnreadNotifications(Long userId, SseEmitter emitter) {
		List<NotificationResponse> unreadList = userNotificationRepository
			.findAllByUserIdAndIsReadFalse(userId)
			.stream().map(NotificationResponse::from).toList();

		sendToClient(emitter, userId, "init", unreadList);
	}

	@Transactional
	public void sendToAllStaff(NotificationEvent event) {
		List<UserEntity> staffList = userRepository.findAllByRole(UserRole.STAFF);

		NotificationEntity notification = NotificationEntity.of(event.content(), event.url(), event.type());
		notificationRepository.save(notification);

		AtomicInteger sendCount = new AtomicInteger(0);

		staffList.forEach(staff -> {
			UserNotificationEntity newUserNotification =
				UserNotificationEntity.createNewNotification(staff, notification);
			userNotificationRepository.save(newUserNotification);

			notificationStore.get(staff.getId()).ifPresent(emitter -> {
				sendToClient(
					emitter,
					staff.getId(),
					"notification",
					NotificationResponse.from(newUserNotification)
				);
				sendCount.incrementAndGet();
			});
		});

		log.debug("로그인 된 STAFF {}명에게 알림 전송 완료.", sendCount.get());
		log.debug("비로그인 STAFF {}명에게 로그인 시 알림이 제공됩니다.", staffList.size() - sendCount.get());
	}

	private void sendToClient(SseEmitter emitter, Long userId, String name, Object data) {
		try {
			emitter.send(SseEmitter.event()
				.id(userId.toString())
				.name(name)
				.data(data));
		} catch (IOException e) {
			notificationStore.deleteById(userId);
			log.debug("SSE 전송 오류", e);
		}
	}

	@Transactional
	public void readNotification(Long userNotificationId) {
		Optional<UserNotificationEntity> findUserNotification = userNotificationRepository.findById(userNotificationId);
		if (findUserNotification.isEmpty()) {
			log.warn("사용자에게 존재하지 않는 알림을 확인처리 합니다. 점검 필요.");
			throw new GlobalException(ErrorCode.NOT_FOUNT_NOTIFICATION);
		}

		UserNotificationEntity userNotification = findUserNotification.get();

		userNotification.markAsRead();
	}
}
