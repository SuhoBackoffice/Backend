package baekgwa.suhoserver.domain.notification.dto;

import java.time.LocalDateTime;

import baekgwa.suhoserver.model.notification.NotificationType;
import baekgwa.suhoserver.model.notification.entity.NotificationEntity;
import baekgwa.suhoserver.model.notification.entity.UserNotificationEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.notification.dto
 * FileName    : NotificationResponse
 * Author      : Baekgwa
 * Date        : 26. 1. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 7.     Baekgwa               Initial creation
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationResponse {
	private Long userNotificationId;
	private String content;
	private String url;
	private NotificationType type;
	private String typeDescription;
	private LocalDateTime createdAt;

	/**
	 * 초기 로딩 시 (미확인 목록 조회) 사용
	 */
	public static NotificationResponse from(UserNotificationEntity userNotification) {
		NotificationEntity notification = userNotification.getNotification();
		return NotificationResponse.builder()
			.userNotificationId(userNotification.getId())
			.content(notification.getContent())
			.url(notification.getUrl())
			.type(notification.getType())
			.typeDescription(notification.getType().getDescription())
			.createdAt(userNotification.getCreatedAt())
			.build();
	}

	/**
	 * 실시간 알림 발송 시 사용
	 */
	public static NotificationResponse from(UserNotificationEntity userNotification, NotificationEntity notification) {
		return NotificationResponse.builder()
			.userNotificationId(userNotification.getId())
			.content(notification.getContent())
			.url(notification.getUrl())
			.type(notification.getType())
			.typeDescription(notification.getType().getDescription())
			.createdAt(notification.getCreatedAt())
			.build();
	}
}
