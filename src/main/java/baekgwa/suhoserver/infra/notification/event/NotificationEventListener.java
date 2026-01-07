package baekgwa.suhoserver.infra.notification.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import baekgwa.suhoserver.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.infra.notification.event
 * FileName    : NotificationEventListener
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
public class NotificationEventListener {

	private final NotificationService notificationService;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleNotificationEvent(NotificationEvent event) {
		log.debug("알림 비동기 처리 시작: {}", event.content());
		notificationService.sendToAllStaff(event);
	}
}
