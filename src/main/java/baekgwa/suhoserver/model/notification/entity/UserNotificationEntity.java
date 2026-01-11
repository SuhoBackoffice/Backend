package baekgwa.suhoserver.model.notification.entity;

import java.time.LocalDateTime;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.notification.entity
 * FileName    : UserNotificationEntity
 * Author      : Baekgwa
 * Date        : 26. 1. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 7.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "user_notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserNotificationEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notification_id", nullable = false)
	private NotificationEntity notification;

	@Column(name = "is_read", nullable = false)
	private boolean isRead;

	@Column(name = "read_at")
	private LocalDateTime readAt;

	@Builder(access = AccessLevel.PRIVATE)
	private UserNotificationEntity(UserEntity user, NotificationEntity notification, boolean isRead,
		LocalDateTime readAt) {
		this.user = user;
		this.notification = notification;
		this.isRead = isRead;
		this.readAt = readAt;
	}

	public static UserNotificationEntity createNewNotification(
		UserEntity user, NotificationEntity notification
	) {
		return UserNotificationEntity
			.builder()
			.user(user)
			.notification(notification)
			.isRead(false)
			.build();
	}

	/**
	 * 알림 확인 상태로 변경
	 */
	public void markAsRead() {
		this.isRead = true;
		this.readAt = LocalDateTime.now();
	}
}
