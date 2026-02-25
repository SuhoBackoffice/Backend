package baekgwa.suhoserver.infra.history.listener;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import baekgwa.suhoserver.global.factory.ProductSerialFactory;
import baekgwa.suhoserver.infra.history.event.ProjectStraightCreatedEvent;
import baekgwa.suhoserver.infra.history.event.ProjectStraightDeletedEvent;
import baekgwa.suhoserver.infra.history.event.ProjectStraightUpdatedEvent;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.project.repository.ProjectRepository;
import baekgwa.suhoserver.model.project.straight.history.entity.ProjectStraightHistoryEntity;
import baekgwa.suhoserver.model.project.straight.history.repository.ProjectStraightHistoryRepository;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.project.straight.straight.repository.ProjectStraightRepository;
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
	private final ProjectRepository projectRepository;
	private final ProjectStraightRepository projectStraightRepository;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void registerProjectStraightHistory(ProjectStraightCreatedEvent event) {
		log.debug("{} Registering Project Straight History", LOG_PREFIX);

		UserEntity user = userRepository.getReferenceById(event.userId());
		ProjectEntity project = projectRepository.getReferenceById(event.projectId());

		List<ProjectStraightHistoryEntity> straightHistoryList = event.projectStraightList().stream()
			.map(ps -> {
				ProjectStraightEntity straight = projectStraightRepository.getReferenceById(ps.projectStraightId());
				return ProjectStraightHistoryEntity.create(
					user,
					project,
					straight,
					ProductSerialFactory.generateStraightSerial(
						ps.length(),
						ps.isLoopRail(),
						ps.straightType()
					),
					ps.totalQuantity()
				);
			})
			.toList();

		projectStraightHistoryRepository.saveAll(straightHistoryList);
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void deleteProjectStraightHistory(ProjectStraightDeletedEvent event) {
		log.debug("{} Deleting Project Straight History", LOG_PREFIX);

		UserEntity user = userRepository.getReferenceById(event.userId());
		ProjectEntity project = projectRepository.getReferenceById(event.projectId());
		ProjectStraightEntity straight = projectStraightRepository.getReferenceById(event.projectStraightId());

		ProjectStraightHistoryEntity history = ProjectStraightHistoryEntity.delete(
			user,
			project,
			straight,
			ProductSerialFactory.generateStraightSerial(event.length(), event.isLoopRail(), event.straightType()),
			event.totalQuantity()
		);

		projectStraightHistoryRepository.saveAndFlush(history);
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void updateProjectStraightHistory(ProjectStraightUpdatedEvent event) {
		log.debug("{} Updating Project Straight History", LOG_PREFIX);

		UserEntity user = userRepository.getReferenceById(event.userId());
		ProjectEntity project = projectRepository.getReferenceById(event.projectId());
		ProjectStraightEntity straight = projectStraightRepository.getReferenceById(event.projectStraightId());

		ProjectStraightHistoryEntity history = ProjectStraightHistoryEntity.update(
			user,
			project,
			straight,
			ProductSerialFactory.generateStraightSerial(event.length(), event.isLoopRail(), event.straightType()),
			event.beforeQuantity(),
			event.afterQuantity()
		);

		projectStraightHistoryRepository.saveAndFlush(history);
	}
}
