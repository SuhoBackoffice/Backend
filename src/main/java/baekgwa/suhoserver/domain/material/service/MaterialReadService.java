package baekgwa.suhoserver.domain.material.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.material.dto.MaterialResponse;
import baekgwa.suhoserver.domain.material.type.MaterialSort;
import baekgwa.suhoserver.model.material.inbound.entity.MaterialInboundEntity;
import baekgwa.suhoserver.model.material.inbound.repository.MaterialInboundRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.material.service
 * FileName    : MaterialReadService
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
public class MaterialReadService {

	private final MaterialInboundRepository materialInboundRepository;

	@Transactional(readOnly = true)
	public List<MaterialResponse.MaterialHistory> getMaterialHistroyList(
		Long projectId, String keyword, MaterialSort sort
	) {
		// 1. keyword 에 매칭되는 모든 material Info 조회
		return materialInboundRepository.findByProjectAndKeyword(projectId, keyword, sort);
	}

	@Transactional(readOnly = true)
	public List<MaterialResponse.MaterialHistoryDetail> getMaterialHistoryDetail(
		Long projectId, String keyword, LocalDate date
	) {
		// 1. materialInbound Entity List 조회
		List<MaterialInboundEntity> findMaterialInboundList =
			materialInboundRepository.findMaterialDetailByKeywordAndDate(projectId, keyword, date);

		// 2. dto 변환 및 return
		return findMaterialInboundList.stream()
			.map(MaterialResponse.MaterialHistoryDetail::of)
			.toList();
	}

	@Transactional(readOnly = true)
	public MaterialResponse.ProjectMaterialState getMaterialState(
		MaterialResponse.ProjectMaterialState projectMaterialState,
		ProjectEntity findProject
	) {
		List<MaterialInboundEntity> findMaterialInboundList = materialInboundRepository.findByProject(findProject);
		long inboundCount = findMaterialInboundList.stream().mapToLong(MaterialInboundEntity::getQuantity).sum();

		return MaterialResponse.ProjectMaterialState.from(projectMaterialState, inboundCount);
	}
}
