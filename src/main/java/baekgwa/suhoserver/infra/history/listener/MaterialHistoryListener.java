package baekgwa.suhoserver.infra.history.listener;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import baekgwa.suhoserver.domain.project.service.ProjectReadService;
import baekgwa.suhoserver.domain.user.service.UserService;
import baekgwa.suhoserver.infra.history.event.MaterialHistoryEvent;
import baekgwa.suhoserver.infra.history.event.MaterialHistoryEventDto;
import baekgwa.suhoserver.model.material.history.entity.MaterialHistoryEntity;
import baekgwa.suhoserver.model.material.history.repository.MaterialHistoryRepository;
import baekgwa.suhoserver.model.material.project.entity.ProjectMaterialStockEntity;
import baekgwa.suhoserver.model.material.project.repository.ProjectMaterialStockRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.infra.history.listener
 * FileName    : MaterialHistoryListener
 * Author      : Baekgwa
 * Date        : 2025-09-20
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-20     Baekgwa               Initial creation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MaterialHistoryListener {

	private final ProjectReadService projectReadService;
	private final UserService userService;
	private final ProjectMaterialStockRepository projectMaterialStockRepository;
	private final MaterialHistoryRepository materialHistoryRepository;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleMaterialHistoryEvent(MaterialHistoryEvent event) {
		ProjectEntity findProject = projectReadService.getProjectOrThrow(event.projectId());
		UserEntity findUser = userService.getUserEntityOrThrow(event.userId());

		Set<Long> stockIds = event.historyList().stream()
			.map(MaterialHistoryEventDto::projectMaterialStockId)
			.collect(Collectors.toSet());

		Map<Long, ProjectMaterialStockEntity> stockMap =
			projectMaterialStockRepository.findAllByProjectAndIdIn(findProject, stockIds)
				.stream()
				.collect(Collectors.toMap(ProjectMaterialStockEntity::getId, Function.identity()));

		List<MaterialHistoryEntity> historyList = event.historyList().stream()
			.map(dto -> {
				ProjectMaterialStockEntity stock = stockMap.get(dto.projectMaterialStockId());
				if (stock == null) {
					log.warn("Material History Event Error : Not Found Stock ID : {}", dto.projectMaterialStockId());
					return null;
				}

				return MaterialHistoryEntity.createNewHistory(
					findProject,
					stock.getMaterialCode(),
					stock.getItemName(),
					dto.quantity(),
					dto.type(),
					generateDescription(findUser, dto)
				);
			})
			.filter(java.util.Objects::nonNull)
			.toList();

		materialHistoryRepository.saveAll(historyList);
	}

	private String generateDescription(UserEntity user, MaterialHistoryEventDto dto) {
		return String.format("%s 님이 %s 을(를) 진행하였습니다.", user.getUsername(), dto.type().getDescription());
	}
}
