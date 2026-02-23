package baekgwa.suhoserver.domain.material.facade;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.material.dto.MaterialRequest;
import baekgwa.suhoserver.domain.material.dto.MaterialResponse;
import baekgwa.suhoserver.domain.material.service.MaterialReadService;
import baekgwa.suhoserver.domain.material.service.MaterialWriteService;
import baekgwa.suhoserver.domain.project.service.ProjectReadService;
import baekgwa.suhoserver.global.response.PageResponse;
import baekgwa.suhoserver.infra.history.event.MaterialHistoryEvent;
import baekgwa.suhoserver.infra.history.event.MaterialHistoryEventDto;
import baekgwa.suhoserver.model.material.MaterialHistoryType;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.material.facade
 * FileName    : MaterialFacade
 * Author      : Baekgwa
 * Date        : 2025-09-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-19     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class MaterialFacade {

	private final ProjectReadService projectReadService;
	private final MaterialWriteService materialWriteService;
	private final MaterialReadService materialReadService;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional(readOnly = true)
	public List<MaterialResponse.SearchMaterialInfo> searchMaterialListByKeyword(Long projectId, String keyword) {
		return materialReadService.searchMaterialListByKeyword(projectId, keyword);
	}

	@Transactional
	public void postMaterialInbound(
		Long projectId,
		List<MaterialRequest.PostMaterialInbound> postMaterialInboundList,
		Long userId
	) {
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);
		materialWriteService.postMaterialInbound(findProject, postMaterialInboundList);

		List<MaterialHistoryEventDto> historyList = postMaterialInboundList.stream()
			.map(req -> new MaterialHistoryEventDto(
				req.getProjectMaterialStockId(),
				req.getQuantity(),
				MaterialHistoryType.INBOUND
			))
			.toList();

		eventPublisher.publishEvent(new MaterialHistoryEvent(projectId, userId, historyList));
	}

	@Transactional(readOnly = true)
	public MaterialResponse.ProjectMaterialState getProjectMaterialState(Long projectId) {
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);
		return materialReadService.getMaterialState(findProject);
	}

	@Transactional(readOnly = true)
	public PageResponse<MaterialResponse.MaterialHistoryInfo> getMaterialHistoryPage(
		Long projectId,
		MaterialRequest.GetMaterialHistory dto
	) {
		projectReadService.getProjectOrThrow(projectId);
		return materialReadService.getMaterialHistoryPage(projectId, dto);
	}

	public List<MaterialResponse.MaterialHistoryTypeInfo> getMaterialHistoryTypes() {
		return Arrays.stream(MaterialHistoryType.values())
			.map(MaterialResponse.MaterialHistoryTypeInfo::from)
			.toList();
	}
}
