package baekgwa.suhoserver.infra.notification.store;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.infra.notification.store
 * FileName    : NotificationStore
 * Author      : Baekgwa
 * Date        : 26. 1. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 7.     Baekgwa               Initial creation
 */
@Component
@Slf4j
public class NotificationStore {

	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	public SseEmitter save(Long userId, SseEmitter emitter) {
		emitters.put(userId, emitter);
		return emitter;
	}

	public void deleteById(Long userId) {
		emitters.remove(userId);
	}

	public Optional<SseEmitter> get(Long userId) {
		return Optional.ofNullable(emitters.get(userId));
	}

	public Map<Long, SseEmitter> findAllEmitters() {
		return new ConcurrentHashMap<>(emitters);
	}
}
