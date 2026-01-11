package baekgwa.suhoserver.infra.notification.event;

import baekgwa.suhoserver.model.notification.NotificationType;

/**
 * PackageName : baekgwa.suhoserver.infra.notification.event
 * FileName    : NotificationEvent
 * Author      : Baekgwa
 * Date        : 26. 1. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 7.     Baekgwa               Initial creation
 */
public record NotificationEvent(
	String content,
	String url,
	NotificationType type
) {

}
