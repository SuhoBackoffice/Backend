package baekgwa.suhoserver.domain.material.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.material.dto.MaterialRequest;
import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.material.history.repository.MaterialHistoryRepository;
import baekgwa.suhoserver.model.material.project.entity.ProjectMaterialStockEntity;
import baekgwa.suhoserver.model.material.project.repository.ProjectMaterialStockRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialWriteService {

	//todo : 신규 material Entity 로 이전 처리 필요.
	// private final MaterialInboundRepository materialInboundRepository;
	private final MaterialHistoryRepository materialHistoryRepository;
	private final ProjectMaterialStockRepository projectMaterialStockRepository;

	@Transactional
	public void postMaterialInbound(
		ProjectEntity findProject,
		List<MaterialRequest.PostMaterialInbound> postMaterialInboundList
	) {
		// // 1. MaterialInbound Entity 생성
		// // 이미 오늘 2번 들어온 자재도, 다른 Row 로 기록.
		// List<MaterialInboundEntity> newMaterialInboundList = postMaterialInboundList.stream()
		// 	.map(data ->
		// 		MaterialInboundEntity.of(data.getDrawingNumber(), data.getItemName(), data.getQuantity(), findProject))
		// 	.toList();
		//
		// // 2. 저장
		// materialInboundRepository.saveAll(newMaterialInboundList);
	}

	/**
	 * 분기레일 자재 (stock) 목록 생성 및 업데이트 진행
	 * 만약 이번에 등록한게 이번 프로젝트에 처음 적용된 거면 신규 row 생성
	 * 이전에 등록된 거라면 수량(totalPlanQuantity) 증가 (음수일 경우 감소)
	 * @param branchTypeIdAndQuantityMap Key: BranchTypeId, Value: 변경될 수량 (양수/음수)
	 * @param branchBomMap key: BranchTypeId, Value: BranchBomList(Items)
	 * @param findProject fk Project
	 */
	@Transactional
	public void updateBranchMaterialPlanStock(
		Map<Long, Long> branchTypeIdAndQuantityMap,
		Map<Long, List<BranchBomEntity>> branchBomMap,
		ProjectEntity findProject
	) {
		Map<String, Long> additionalQuantityMap = new HashMap<>();
		Map<String, String> itemNameMap = new HashMap<>();

		for (Map.Entry<Long, Long> entry : branchTypeIdAndQuantityMap.entrySet()) {
			Long branchTypeId = entry.getKey();
			Long quantity = entry.getValue();

			List<BranchBomEntity> bomList = branchBomMap.get(branchTypeId);
			if (bomList == null || bomList.isEmpty()) {
				log.warn("BOM 이 등록되지 않은 분기레일이 존재? branchTypeId: {}", branchTypeId);
				continue;
			}

			for (BranchBomEntity bom : bomList) {
				long requiredAmount = bom.getUnitQuantity() * quantity;
				additionalQuantityMap.merge(bom.getDrawingNumber(), requiredAmount, Long::sum);
				itemNameMap.putIfAbsent(bom.getDrawingNumber(), bom.getItemName());
			}
		}

		List<ProjectMaterialStockEntity> needUpdateStockList =
			projectMaterialStockRepository.findExistMaterialStockList(findProject, additionalQuantityMap.keySet());

		Map<String, ProjectMaterialStockEntity> existingStockMap = needUpdateStockList.stream()
			.collect(Collectors.toMap(ProjectMaterialStockEntity::getMaterialCode, Function.identity()));

		List<ProjectMaterialStockEntity> newStockList = new ArrayList<>();

		additionalQuantityMap.forEach((drawingNumber, addQuantity) -> {
			if (existingStockMap.containsKey(drawingNumber)) {
				ProjectMaterialStockEntity stock = existingStockMap.get(drawingNumber);
				stock.addTotalPlanQuantity(addQuantity);
			} else {
				newStockList.add(
					ProjectMaterialStockEntity.createNewBranchStock(findProject, drawingNumber,
						itemNameMap.get(drawingNumber), addQuantity));
			}
		});

		if (!newStockList.isEmpty()) {
			projectMaterialStockRepository.saveAll(newStockList);
		}
	}

	/**
	 * 분기레일 자재 (stock) 사용 처리
	 * @param branchTypeIdAndQuantityMap Key: BranchTypeId, Value: 작업 완료한 수량 (양수)
	 * @param branchBomMap key: BranchTypeId, Value: BranchBomList(Items)
	 * @param findProject fk Project
	 */
	@Transactional
	public void updateBranchMaterialCompleteStock(
		Map<Long, Long> branchTypeIdAndQuantityMap,
		Map<Long, List<BranchBomEntity>> branchBomMap,
		ProjectEntity findProject
	) {
		Map<String, Long> usedQuantityMap = new HashMap<>();

		for (Map.Entry<Long, Long> entry : branchTypeIdAndQuantityMap.entrySet()) {
			Long branchTypeId = entry.getKey();
			Long quantity = entry.getValue();

			List<BranchBomEntity> bomList = branchBomMap.get(branchTypeId);
			if (bomList == null || bomList.isEmpty()) {
				log.warn("BOM 이 등록되지 않은 분기레일 생산 보고? branchTypeId: {}", branchTypeId);
				continue;
			}

			for (BranchBomEntity bom : bomList) {
				long usedAmount = bom.getUnitQuantity() * quantity;
				usedQuantityMap.merge(bom.getDrawingNumber(), usedAmount, Long::sum);
			}
		}

		List<ProjectMaterialStockEntity> needUpdateStockList =
			projectMaterialStockRepository.findExistMaterialStockList(findProject, usedQuantityMap.keySet());

		Map<String, ProjectMaterialStockEntity> existingStockMap = needUpdateStockList.stream()
			.collect(Collectors.toMap(ProjectMaterialStockEntity::getMaterialCode, Function.identity()));

		usedQuantityMap.forEach((drawingNumber, usedQuantity) -> {
			ProjectMaterialStockEntity stock = existingStockMap.get(drawingNumber);
			stock.addTotalUsedQuantity(usedQuantity);
		});
	}
}
