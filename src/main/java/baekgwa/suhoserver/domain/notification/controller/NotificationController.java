package baekgwa.suhoserver.domain.notification.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import baekgwa.suhoserver.domain.notification.service.NotificationReadService;
import baekgwa.suhoserver.domain.notification.service.NotificationWriteService;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.notification.controller
 * FileName    : NotificationController
 * Author      : Baekgwa
 * Date        : 26. 1. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 7.     Baekgwa               Initial creation
 */
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationReadService notificationReadService;
	private final NotificationWriteService notificationWriteService;

	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<SseEmitter> subscribe(
		@AuthenticationPrincipal Long userId
	) {
		SseEmitter emitter = notificationReadService.subscribe(userId);
		return ResponseEntity.ok(emitter);
	}

	@PatchMapping("/{userNotificationId}/read")
	public BaseResponse<Void> readNotification(
		@PathVariable("userNotificationId") Long userNotificationId
	) {
		notificationWriteService.markAsRead(userNotificationId);
		return BaseResponse.success(SuccessCode.DELETE_FILE_SUCCESS);
	}
}
