package baekgwa.suhoserver.infra.history.listener;

import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import baekgwa.suhoserver.global.factory.ProductSerialFactory;
import baekgwa.suhoserver.infra.history.event.ProjectStraightCreatedEvent;
import baekgwa.suhoserver.infra.history.event.ProjectStraightDeletedEvent;
import baekgwa.suhoserver.model.project.straight.history.entity.ProjectStraightHistoryEntity;
import baekgwa.suhoserver.model.project.straight.history.repository.ProjectStraightHistoryRepository;
import baekgwa.suhoserver.model.user.entity.UserEntity;
import baekgwa.suhoserver.model.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.infra.history.listener
 * FileName    : ProjectStraightHistoryListener
 * Author      : Baekgwa
 * Date        : 25. 12. 26.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 26.     Baekgwa               Initial creation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectStraightHistoryListener {

	private static final String LOG_PREFIX = "[Project Straight History Register]";

	private final ProjectStraightHistoryRepository projectStraightHistoryRepository;
	private final UserRepository userRepository;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void registerProjectStraightHistory(ProjectStraightCreatedEvent event) {
		log.debug("{} Registering Project Straight History", LOG_PREFIX);

		Optional<UserEntity> optionalFindUser = userRepository.findById(event.userId());
		if (optionalFindUser.isEmpty()) {
			log.warn("{} 회원 정보를 찾을 수 없어, history 저장을 종료합니다. event = {}", LOG_PREFIX, event);
			return;
		}
		UserEntity findUser = optionalFindUser.get();

		List<ProjectStraightHistoryEntity> straightHistoryList = event.projectStraightList().stream()
			.map(ps -> ProjectStraightHistoryEntity.create(
				findUser.getId(),
				findUser.getUsername(),
				event.projectId(),
				ps.projectStraightId(),
				ProductSerialFactory.generateStraightSerial(
					ps.length(),
					ps.isLoopRail(),
					ps.straightType()
				),
				ps.totalQuantity()
			))
			.toList();

		projectStraightHistoryRepository.saveAll(straightHistoryList);
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void deleteProjectStraightHistory(ProjectStraightDeletedEvent event) {
		log.debug("{} Deleting Project Straight History", LOG_PREFIX);

		Optional<UserEntity> optionalFindUser = userRepository.findById(event.userId());
		if (optionalFindUser.isEmpty()) {
			log.warn("{} 회원 정보를 찾을 수 없어, history 저장을 종료합니다. event = {}", LOG_PREFIX, event);
			return;
		}
		UserEntity findUser = optionalFindUser.get();

		ProjectStraightHistoryEntity history = ProjectStraightHistoryEntity.delete(
			findUser.getId(),
			findUser.getUsername(),
			event.projectId(),
			event.projectStraightId(),
			ProductSerialFactory.generateStraightSerial(event.length(), event.isLoopRail(), event.straightType()),
			event.totalQuantity()
		);

		projectStraightHistoryRepository.saveAndFlush(history);
	}
}
