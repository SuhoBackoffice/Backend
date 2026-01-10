package baekgwa.suhoserver.infra.notification.event;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import baekgwa.suhoserver.domain.notification.dto.NotificationResponse;
import baekgwa.suhoserver.domain.notification.service.NotificationPushService;
import baekgwa.suhoserver.domain.notification.service.NotificationWriteService;
import baekgwa.suhoserver.model.notification.entity.UserNotificationEntity;
import baekgwa.suhoserver.model.user.entity.UserRole;
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

	private final NotificationWriteService notificationWriteService;
	private final NotificationPushService notificationPushService;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleWorkReportEvent(NotificationEvent event) {
		log.debug("업무 보고 알림 처리 시작: {}", event.content());

		List<UserNotificationEntity> savedNotifications =
			notificationWriteService.createBulkNotification(event, UserRole.STAFF);

		savedNotifications.forEach(mapping -> notificationPushService.send(
			mapping.getUser().getId(),
			"notification",
			NotificationResponse.from(mapping)
		));
	}
}
