package baekgwa.suhoserver.domain.material.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.material.dto.MaterialRequest;
import baekgwa.suhoserver.domain.material.dto.MaterialResponse;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.global.response.PageResponse;
import baekgwa.suhoserver.model.material.history.repository.MaterialHistoryRepository;
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
	private final MaterialHistoryRepository materialHistoryRepository;

	@Transactional(readOnly = true)
	public List<MaterialResponse.SearchMaterialInfo> searchMaterialListByKeyword(Long projectId, String keyword) {
		List<ProjectMaterialStockEntity> stockList =
			projectMaterialStockRepository.searchByProjectAndKeyword(projectId, keyword);

		return stockList.stream()
			.map(MaterialResponse.SearchMaterialInfo::from)
			.toList();
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
	 * 프로젝트 자재 이력 페이징 조회
	 * @param projectId 프로젝트 PK
	 * @param dto 페이징 및 필터 조건
	 * @return 자재 이력 페이징 결과
	 */
	@Transactional(readOnly = true)
	public PageResponse<MaterialResponse.MaterialHistoryInfo> getMaterialHistoryPage(
		Long projectId,
		MaterialRequest.GetMaterialHistory dto
	) {
		if (dto.getPage() < 0 || dto.getSize() < 1) {
			throw new GlobalException(ErrorCode.INVALID_PAGINATION_PARAMETER);
		}

		Page<MaterialResponse.MaterialHistoryInfo> result =
			materialHistoryRepository.searchHistoryList(projectId, dto);

		return PageResponse.of(result);
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
