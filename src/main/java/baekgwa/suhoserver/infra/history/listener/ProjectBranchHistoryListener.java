package baekgwa.suhoserver.infra.history.listener;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import baekgwa.suhoserver.global.factory.ProductSerialFactory;
import baekgwa.suhoserver.infra.history.event.ProjectBranchCreatedEvent;
import baekgwa.suhoserver.infra.history.event.ProjectBranchDeletedEvent;
import baekgwa.suhoserver.infra.history.event.ProjectBranchUpdatedEvent;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.branch.repository.ProjectBranchRepository;
import baekgwa.suhoserver.model.project.branch.history.entity.ProjectBranchHistoryEntity;
import baekgwa.suhoserver.model.project.branch.history.repository.ProjectBranchHistoryRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.project.repository.ProjectRepository;
import baekgwa.suhoserver.model.user.entity.UserEntity;
import baekgwa.suhoserver.model.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.infra.history.listener
 * FileName    : ProjectBranchHistoryListener
 * Author      : Baekgwa
 * Date        : 25. 12. 27.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 27.     Baekgwa               Initial creation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectBranchHistoryListener {

	private static final String LOG_PREFIX = "[Project Branch History Register]";

	private final ProjectBranchHistoryRepository projectBranchHistoryRepository;
	private final UserRepository userRepository;
	private final ProjectRepository projectRepository;
	private final ProjectBranchRepository projectBranchRepository;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void registerProjectBranchHistory(ProjectBranchCreatedEvent event) {
		log.debug("{} Registering Project Branch History", LOG_PREFIX);

		UserEntity findUser = userRepository.getReferenceById(event.userId());
		ProjectEntity project = projectRepository.getReferenceById(event.projectId());

		List<ProjectBranchHistoryEntity> historyList = event.projectBranchDtoList().stream()
			.map(pb -> {
				ProjectBranchEntity projectBranch = projectBranchRepository.getReferenceById(pb.projectBranchId());
				return ProjectBranchHistoryEntity.create(
					findUser,
					project,
					projectBranch,
					ProductSerialFactory.generateBranchSerial(pb.code()),
					pb.afterQuantity()
				);
			})
			.toList();

		projectBranchHistoryRepository.saveAll(historyList);
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void deleteProjectBranchHistory(ProjectBranchDeletedEvent event) {
		log.debug("{} Deleting Project Branch History", LOG_PREFIX);

		UserEntity findUser = userRepository.getReferenceById(event.userId());
		ProjectEntity project = projectRepository.getReferenceById(event.projectId());
		ProjectBranchEntity projectBranch = projectBranchRepository.getReferenceById(event.projectBranchId());

		ProjectBranchHistoryEntity history = ProjectBranchHistoryEntity.delete(
			findUser,
			project,
			projectBranch,
			ProductSerialFactory.generateBranchSerial(event.code()),
			event.beforeQuantity()
		);

		projectBranchHistoryRepository.save(history);
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void updateProjectBranchHistory(ProjectBranchUpdatedEvent event) {
		log.debug("{} Updating Project Branch History", LOG_PREFIX);

		UserEntity findUser = userRepository.getReferenceById(event.userId());
		ProjectEntity project = projectRepository.getReferenceById(event.projectId());
		ProjectBranchEntity projectBranch = projectBranchRepository.getReferenceById(event.projectBranchId());

		ProjectBranchHistoryEntity history = ProjectBranchHistoryEntity.update(
			findUser,
			project,
			projectBranch,
			ProductSerialFactory.generateBranchSerial(event.code()),
			event.beforeQuantity(),
			event.afterQuantity()
		);

		projectBranchHistoryRepository.save(history);
	}
}
