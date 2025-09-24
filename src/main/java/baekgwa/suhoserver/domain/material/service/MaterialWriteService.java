package baekgwa.suhoserver.domain.material.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.material.dto.MaterialRequest;
import baekgwa.suhoserver.model.material.inbound.entity.MaterialInboundEntity;
import baekgwa.suhoserver.model.material.inbound.repository.MaterialInboundRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.material.service
 * FileName    : MaterialWriteService
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
public class MaterialWriteService {

	private final MaterialInboundRepository materialInboundRepository;

	@Transactional
	public void postMaterialInbound(
		ProjectEntity findProject,
		List<MaterialRequest.PostMaterialInbound> postMaterialInboundList
	) {
		// 1. MaterialInbound Entity 생성
		// 이미 오늘 2번 들어온 자재도, 다른 Row 로 기록.
		List<MaterialInboundEntity> newMaterialInboundList = postMaterialInboundList.stream()
			.map(data ->
				MaterialInboundEntity.of(data.getDrawingNumber(), data.getItemName(), data.getQuantity(), findProject))
			.toList();

		// 2. 저장
		materialInboundRepository.saveAll(newMaterialInboundList);
	}
}
