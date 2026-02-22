package baekgwa.suhoserver.domain.material.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.material.dto.MaterialResponse;
import baekgwa.suhoserver.domain.material.type.MaterialSort;
import baekgwa.suhoserver.model.material.project.entity.ProjectMaterialStockEntity;
import baekgwa.suhoserver.model.material.project.repository.ProjectMaterialStockRepository;
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

	private final ProjectMaterialStockRepository projectMaterialStockRepository;

	@Transactional(readOnly = true)
	public List<MaterialResponse.SearchMaterialInfo> searchMaterialListByKeyword(Long projectId, String keyword) {
		List<ProjectMaterialStockEntity> stockList =
			projectMaterialStockRepository.searchByProjectAndKeyword(projectId, keyword);

		return stockList.stream()
			.map(MaterialResponse.SearchMaterialInfo::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<MaterialResponse.MaterialHistory> getMaterialHistroyList(
		Long projectId, String keyword, MaterialSort sort
	) {
		// 1. keyword 에 매칭되는 모든 material Info 조회
		// return materialInboundRepository.findByProjectAndKeyword(projectId, keyword, sort);
		return List.of();
	}

	@Transactional(readOnly = true)
	public List<MaterialResponse.MaterialHistoryDetail> getMaterialHistoryDetail(
		Long projectId, String keyword, LocalDate date
	) {
		// // 1. materialInbound Entity List 조회
		// List<MaterialInboundEntity> findMaterialInboundList =
		// 	materialInboundRepository.findMaterialDetailByKeywordAndDate(projectId, keyword, date);
		//
		// // 2. dto 변환 및 return
		// return findMaterialInboundList.stream()
		// 	.map(MaterialResponse.MaterialHistoryDetail::of)
		// 	.toList();
		return List.of();
	}

	@Transactional(readOnly = true)
	public MaterialResponse.ProjectMaterialState getMaterialState(ProjectEntity findProject) {
		List<ProjectMaterialStockEntity> stockList = projectMaterialStockRepository.findAllByProject(findProject);

		long unitKindCount = stockList.size();
		long totalCount = stockList.stream().mapToLong(ProjectMaterialStockEntity::getTotalPlanQuantity).sum();
		long usedCount = stockList.stream().mapToLong(ProjectMaterialStockEntity::getTotalUsedQuantity).sum();
		long inboundCount = stockList.stream().mapToLong(ProjectMaterialStockEntity::getTotalInboundQuantity).sum();

		BigDecimal inboundPercent = BigDecimal.ZERO;
		if (totalCount > 0) {
			inboundPercent = BigDecimal.valueOf(inboundCount)
				.multiply(BigDecimal.valueOf(100))
				.divide(BigDecimal.valueOf(totalCount), 1, RoundingMode.HALF_UP);
		}

		return MaterialResponse.ProjectMaterialState
			.from(inboundPercent, unitKindCount, totalCount, inboundCount, usedCount);
	}

	/**
	 * 프로젝트에 입고된 모든 자재 조회 후, 도번 기준으로 수량 정리
	 * @param projectId 프로젝트 PK
	 * @return Map<도번, 수량>
	 */
	public Map<String, Long> getAllProjectMaterial(Long projectId) {
		// List<MaterialInboundEntity> findMaterialList = materialInboundRepository.findByProjectId(projectId);
		// return findMaterialList.stream()
		// 	.collect(Collectors.groupingBy(
		// 		MaterialInboundEntity::getDrawingNumber,
		// 		Collectors.summingLong(MaterialInboundEntity::getQuantity)
		// 	));
		return Map.of();
	}
}
