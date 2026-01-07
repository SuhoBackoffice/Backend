package baekgwa.suhoserver.model.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.notification
 * FileName    : NotificationType
 * Author      : Baekgwa
 * Date        : 26. 1. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 7.     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum NotificationType {

	WORK_REPORT("업무 보고"),
	NOTICE("공지 사항");

	private final String description;
}
