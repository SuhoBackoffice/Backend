package baekgwa.suhoserver.domain.material.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.material.dto.MaterialRequest;
import baekgwa.suhoserver.global.factory.StraightBomInfoFactory;
import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.material.history.repository.MaterialHistoryRepository;
import baekgwa.suhoserver.model.material.project.entity.ProjectMaterialStockEntity;
import baekgwa.suhoserver.model.material.project.repository.ProjectMaterialStockRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.bom.entity.ProjectStraightBomEntity;
import baekgwa.suhoserver.model.project.straight.bom.entity.ProjectStraightBomRuleEntity;
import baekgwa.suhoserver.model.project.straight.bom.repository.ProjectStraightBomRepository;
import baekgwa.suhoserver.model.project.straight.bom.repository.ProjectStraightBomRuleRepository;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.straight.StraightBomConditionType;
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
	private final ProjectStraightBomRuleRepository projectStraightBomRuleRepository;
	private final ProjectStraightBomRepository projectStraightBomRepository;

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

	/**
	 * 직선레일 BOM 생성 및 저장
	 * @param saveProjectStraightList
	 * @param project
	 * @return
	 */
	@Transactional
	public List<ProjectStraightBomEntity> createProjectStraightBom(
		List<ProjectStraightEntity> saveProjectStraightList,
		ProjectEntity project
	) {
		//rules 가져오기
		List<ProjectStraightBomRuleEntity> straightBomRule = projectStraightBomRuleRepository.findAllByProject(project);
		Map<StraightBomConditionType, List<ProjectStraightBomRuleEntity>> bomConditionMap = straightBomRule.stream()
			.collect(Collectors.groupingBy(
				ProjectStraightBomRuleEntity::getConditionType,
				Collectors.toList()
			));

		List<ProjectStraightBomEntity> bomList = new ArrayList<>();

		// 직선레일 한개를 제작하는데 필요한 BOM List 생성
		for (ProjectStraightEntity straight : saveProjectStraightList) {
			List<ProjectStraightBomEntity> tempBomList = new ArrayList<>();

			// 프로파일 생성
			tempBomList.add(
				ProjectStraightBomEntity.of(
					straight,
					StraightBomInfoFactory.generateProfileMaterialCode(straight.getLength(),
						straight.getHolePosition()),
					StraightBomInfoFactory.generateProfileItemName(straight.getLength(), straight.getIsLoopRail(),
						project.getVersionInfoEntity().getName()),
					2L //2개씩 들어감
				)
			);

			// Yoke 관련 추가
			tempBomList.addAll(
				generateYokeBom(
					straight,
					bomConditionMap.get(StraightBomConditionType.YOKE)
				)
			);

			// Litzwire 자체 및 체결을 위한 클램프 추가
			tempBomList.addAll(
				generateLitzwireBom(
					project,
					straight,
					bomConditionMap.get(StraightBomConditionType.LITZ_WIRE)
				)
			);

			// 루프용 자재 추가
			if (Boolean.TRUE.equals(straight.getIsLoopRail())) {
				tempBomList.addAll(
					generateLoopLitzwireBom(
						straight,
						bomConditionMap.get(StraightBomConditionType.LOOP_LITZ_WIRE))
				);
			}

			// 중복 자재 병합
			bomList.addAll(mergeBomList(straight, tempBomList));
		}

		return projectStraightBomRepository.saveAll(bomList);
	}

	@Transactional
	public void updateStraightMaterialPlanStock(
		List<ProjectStraightBomEntity> savedStraightBom,
		ProjectEntity findProject
	) {
		Map<String, Long> additionalQuantityMap = new HashMap<>();
		Map<String, String> itemNameMap = new HashMap<>();

		for (ProjectStraightBomEntity bom : savedStraightBom) {
			long requiredAmount = bom.getUnitQuantity() * bom.getProjectStraight().getTotalQuantity();
			additionalQuantityMap.merge(bom.getMaterialCode(), requiredAmount, Long::sum);
			itemNameMap.putIfAbsent(bom.getMaterialCode(), bom.getItemName());
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

	private List<ProjectStraightBomEntity> generateLoopLitzwireBom(
		ProjectStraightEntity straight,
		List<ProjectStraightBomRuleEntity> ruleList
	) {
		if (ruleList == null || ruleList.isEmpty()) {
			return List.of();
		}

		return ruleList.stream()
			.map(rule -> ProjectStraightBomEntity.of(
				straight,
				rule.getMaterialCode(),
				rule.getItemName(),
				rule.getQuantity()
			)).toList();
	}

	private List<ProjectStraightBomEntity> mergeBomList(
		ProjectStraightEntity straight,
		List<ProjectStraightBomEntity> bomList
	) {
		Map<String, Long> quantityMap = new HashMap<>();
		Map<String, String> nameMap = new HashMap<>();

		for (ProjectStraightBomEntity bom : bomList) {
			quantityMap.merge(bom.getMaterialCode(), bom.getUnitQuantity(), Long::sum);
			nameMap.putIfAbsent(bom.getMaterialCode(), bom.getItemName());
		}

		return quantityMap.entrySet().stream()
			.map(entry -> ProjectStraightBomEntity.of(
				straight,
				entry.getKey(),
				nameMap.get(entry.getKey()),
				entry.getValue()
			))
			.toList();
	}

	private List<ProjectStraightBomEntity> generateLitzwireBom(
		ProjectEntity project,
		ProjectStraightEntity straight,
		List<ProjectStraightBomRuleEntity> ruleList
	) {
		List<ProjectStraightBomEntity> result = new ArrayList<>();

		result.addAll(generateLitzwireBomList(straight.getLitzwire1(), straight, project, ruleList));
		result.addAll(generateLitzwireBomList(straight.getLitzwire2(), straight, project, ruleList));
		result.addAll(generateLitzwireBomList(straight.getLitzwire3(), straight, project, ruleList));
		result.addAll(generateLitzwireBomList(straight.getLitzwire4(), straight, project, ruleList));
		result.addAll(generateLitzwireBomList(straight.getLitzwire5(), straight, project, ruleList));
		result.addAll(generateLitzwireBomList(straight.getLitzwire6(), straight, project, ruleList));

		return result;
	}

	private List<ProjectStraightBomEntity> generateLitzwireBomList(
		BigDecimal litzwire,
		ProjectStraightEntity straight,
		ProjectEntity project,
		List<ProjectStraightBomRuleEntity> ruleList
	) {
		List<ProjectStraightBomEntity> result = new ArrayList<>();

		if (litzwire != null &&
			litzwire.doubleValue() > 0
		) {
			// litzwire 자체 추가
			result.add(ProjectStraightBomEntity.of(
				straight,
				StraightBomInfoFactory.generateLitzwireMaterialCode(litzwire),
				StraightBomInfoFactory.generateLitzwireItemName(project.getVersionInfoEntity().getName(), litzwire),
				1L // 1개 만큼만 증가
			));

			// Litzwire 를 체결하기 위한 부품 추가. 구간(길이)별 수량이 차이남
			result.addAll(
				ruleList.stream()
					.filter(rule -> rule.getMinConditionValue().compareTo(litzwire) <= 0)
					.filter(rule -> rule.getMaxConditionValue().compareTo(litzwire) > 0)
					.map(rule -> ProjectStraightBomEntity.of(
						straight,
						rule.getMaterialCode(),
						rule.getItemName(),
						rule.getQuantity()
					)).toList());
		}

		return result;
	}

	/**
	 * 길이별로 rule 을 확인하여 MaterialStock 을 생성
	 * @param straight 직선레일
	 * @param ruleList Yoke 관련 BOM Rule List
	 * @return 생성된 ProjectMaterialStockEntity List
	 */
	private List<ProjectStraightBomEntity> generateYokeBom(
		ProjectStraightEntity straight,
		List<ProjectStraightBomRuleEntity> ruleList
	) {
		if (ruleList == null || ruleList.isEmpty()) {
			return List.of();
		}

		BigDecimal lengthDecimal = BigDecimal.valueOf(straight.getLength());

		return ruleList.stream()
			.filter(rule -> rule.getMinConditionValue().compareTo(lengthDecimal) <= 0) //이상
			.filter(rule -> rule.getMaxConditionValue().compareTo(lengthDecimal) > 0) //미만
			.findFirst()
			.map(rule -> ProjectStraightBomEntity.of(
				straight,
				rule.getMaterialCode(),
				rule.getItemName(),
				rule.getQuantity()
			)).map(List::of)
			.orElseGet(List::of);
	}
}
