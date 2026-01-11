package baekgwa.suhoserver.model.notification.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.model.notification.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.notification.entity
 * FileName    : NotificationEntity
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
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "url")
	private String url;

	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificationType type;

	@Builder(access = AccessLevel.PRIVATE)
	private NotificationEntity(String content, String url, NotificationType type) {
		this.content = content;
		this.url = url;
		this.type = type;
	}

	public static NotificationEntity of(String content, String url, NotificationType type) {
		return NotificationEntity
			.builder()
			.content(content)
			.url(url)
			.type(type)
			.build();
	}
}
