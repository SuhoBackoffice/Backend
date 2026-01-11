package baekgwa.suhoserver.domain.notification.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import baekgwa.suhoserver.infra.notification.store.NotificationStore;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.notification.service
 * FileName    : NotificationPushService
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
public class NotificationPushService {

	private final NotificationStore notificationStore;

	/**
	 * 특정 유저에게 이벤트를 전송합니다.
	 * @param userId 회원 PK
	 * @param eventName 이벤트 이름
	 * @param data 전송할 내용
	 */
	public void send(Long userId, String eventName, Object data) {
		notificationStore.get(userId).ifPresent(emitter -> {
			try {
				emitter.send(SseEmitter.event()
					.id(userId.toString())
					.name(eventName)
					.data(data));
			} catch (IOException e) {
				notificationStore.deleteById(userId);
			}
		});
	}

	/**
	 * 다수에게 동일한 이름의 이벤트를 전송합니다.
	 * @param userIds 회원 PK List
	 * @param eventName 이벤트 이름 (init 등)
	 * @param data
	 */
	public void sendBulk(List<Long> userIds, String eventName, Object data) {
		userIds.forEach(id -> send(id, eventName, data));
	}
}