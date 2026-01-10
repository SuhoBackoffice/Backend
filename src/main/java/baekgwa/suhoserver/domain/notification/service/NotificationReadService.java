package baekgwa.suhoserver.domain.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import baekgwa.suhoserver.domain.notification.dto.NotificationResponse;
import baekgwa.suhoserver.infra.notification.store.NotificationStore;
import baekgwa.suhoserver.model.notification.repository.UserNotificationRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.notification.service
 * FileName    : NotificationReadService
 * Author      : Baekgwa
 * Date        : 26. 1. 10.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 10.     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationReadService {

	private final UserNotificationRepository userNotificationRepository;
	private final NotificationStore notificationStore;
	private final NotificationPushService notificationPushService;

	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간

	/**
	 * SSE 구독을 처리하고 초기 데이터를 전송합니다.
	 * @param userId 회원 PK
	 * @return
	 */
	public SseEmitter subscribe(Long userId) {
		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

		emitter.onCompletion(() -> notificationStore.deleteById(userId));
		emitter.onTimeout(() -> notificationStore.deleteById(userId));

		notificationStore.save(userId, emitter);

		sendInitialUnreadNotifications(userId);

		return emitter;
	}

	/**
	 * 초기 접속 시 읽지 않은 알림 목록 조회 후 전달
	 * @param userId
	 */
	private void sendInitialUnreadNotifications(Long userId) {
		List<NotificationResponse> unreadList = userNotificationRepository
			.findAllByUserIdAndIsReadFalse(userId)
			.stream()
			.map(NotificationResponse::from)
			.toList();

		// PushService를 사용하여 전송
		notificationPushService.send(userId, "init", unreadList);
	}
}