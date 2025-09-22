package baekgwa.suhoserver.domain.branch.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.material.dto.MaterialResponse;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.branch.bom.repository.BranchBomRepository;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import baekgwa.suhoserver.model.branch.type.repository.BranchTypeRepository;
import baekgwa.suhoserver.model.project.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.repository.ProjectBranchRepository;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.branch.service
 * FileName    : BranchReadService
 * Author      : Baekgwa
 * Date        : 2025-09-14
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-14     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class BranchReadService {

	private final BranchTypeRepository branchTypeRepository;
	private final BranchBomRepository branchBomRepository;
	private final ProjectBranchRepository projectBranchRepository;

	/**
	 * 동일 프로젝트 버전, 분기코드, Bom 버전으로 업데이트 된 적이 있는지 확인
	 * @param versionInfoEntity 프로젝트 버전
	 * @param branchCode 분기 코드
	 */
	@Transactional(readOnly = true)
	public boolean isTodayUpdated(VersionInfoEntity versionInfoEntity, String branchCode) {
		return branchTypeRepository.existsByParams(
			versionInfoEntity.getId(), branchCode, BranchTypeEntity.generateVersion());
	}

	/**
	 * 해당 분기레일의 최신 분기 레일 정보 조회
	 * @param versionInfoId 버전 정보 PK
	 * @param branchCode 분기레일 식별 코드
	 * @return 찾은 분기레일 타입
	 */
	@Transactional(readOnly = true)
	public BranchTypeEntity getLatestBranchTypeOrThrow(Long versionInfoId, String branchCode) {
		return branchTypeRepository.findLatest(versionInfoId, branchCode, PageRequest.of(0, 1))
			.stream()
			.findFirst()
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_BRANCH_BOM));
	}

	/**
	 * 분기 정보 Entity 로, BOM List 정보 조회
	 * @param branchTypeEntity 분기레일 정보
	 * @return 분기레일 BOM List
	 */
	@Transactional(readOnly = true)
	public List<BranchBomEntity> getBranchBomListOrThrow(BranchTypeEntity branchTypeEntity) {
		List<BranchBomEntity> findBranchBomList = branchBomRepository.findByBranchTypeEntity(branchTypeEntity);

		if (findBranchBomList.isEmpty()) {
			throw new GlobalException(ErrorCode.NOT_FOUND_BRANCH_BOM);
		}

		return findBranchBomList;
	}

	/**
	 * 분기 정보 Entity 조회
	 * @param branchTypeId 분기 정보 Entity PK
	 * @return 분기 정보 Entity
	 */
	@Transactional(readOnly = true)
	public BranchTypeEntity getBranchTypeOrThrow(Long branchTypeId) {
		return branchTypeRepository.findById(branchTypeId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_BRANCH_TYPE));
	}

	/**
	 * branchIdSet 으로, Map<PK, Entity> 를 반환하는 메서드
	 * @param branchIdSet 분기레일 PK Set
	 * @return Map<PK, Entity>
	 */
	@Transactional(readOnly = true)
	public Map<Long, BranchTypeEntity> getBranchTypeListOrThrow(Set<Long> branchIdSet) {
		List<BranchTypeEntity> findBranchTypeList = branchTypeRepository.findAllById(branchIdSet);

		if (branchIdSet.size() != findBranchTypeList.size()) {
			throw new GlobalException(ErrorCode.NOT_FOUND_BRANCH_TYPE);
		}

		return findBranchTypeList.stream()
			.collect(Collectors.toMap(BranchTypeEntity::getId, Function.identity()));
	}

	/**
	 * 분기 타입 ID List 로, 전체 BomList 중, Keyword 에 일치하는 자재 목록을 반환
	 * keyword 는, [도번, 자재 명]에서 검색
	 * @param findBranchTypeIdList 분기 타입 ID List
	 * @param keyword 검색 키워드
	 * @return 찾은 BOM List
	 */
	@Transactional(readOnly = true)
	public List<MaterialResponse.MaterialInfo> getAllBranchBomList(List<Long> findBranchTypeIdList, String keyword) {
		// 1. 분기 타입 중, keyword 와 매칭되는 List 검색
		List<BranchBomEntity> findBranchBomList = branchBomRepository.searchBranchBomList(findBranchTypeIdList,
			keyword);

		// 2. DTO 응답
		return findBranchBomList.stream()
			.map(MaterialResponse.MaterialInfo::of)
			.toList();
	}

	/**
	 * 프로젝트 분기레일 목록을 통해, 모든 분기레일 자재를 정리하는 메서드
	 */
	@Transactional(readOnly = true)
	public MaterialResponse.ProjectMaterialState getBranchBomList(List<ProjectBranchEntity> findProjectBranchList) {
		// 1. 분기 목록에 해당하는 id 별, branch_type 정보 추출
		// key = projectTypeId, value = ProjectBranch Entity
		// 추후, value 값으로, target_quantity, completed_quantity 계산해야함.
		Map<Long, ProjectBranchEntity> findProjectTypeIdMap = findProjectBranchList.stream()
			.collect(Collectors.toMap(data -> data.getBranchType().getId(), Function.identity()));

		// 2) 모든 분기 타입의 BOM을 한 번에 조회 (N+1 방지)
		List<Long> typeIds = new ArrayList<>(findProjectTypeIdMap.keySet());
		List<BranchBomEntity> branchBomList = branchBomRepository.findAllByBranchTypeIds(typeIds);

		// 3) 집계
		long totalCount = 0L; // 필요한 총 수량
		long usedCount = 0L; // 사용(완료) 총 수량
		Set<String> uniqueKinds = new HashSet<>(); // drawingNumber 기준 종류 수

		for (BranchBomEntity bom : branchBomList) {
			ProjectBranchEntity findProjectBranch = findProjectTypeIdMap.get(bom.getBranchTypeEntity().getId());
			// 총 필요 수량 = unitQuantity × 계획 수량
			totalCount += bom.getUnitQuantity() * findProjectBranch.getTotalQuantity();
			// 사용 수량 = unitQuantity × 완료 수량
			usedCount += bom.getUnitQuantity() * findProjectBranch.getCompletedQuantity();
			// 종류 수(도번 기준)
			uniqueKinds.add(bom.getDrawingNumber());
		}

		long unitKindCount = uniqueKinds.size();

		// 4) 이 메서드 역할은 3가지만 채우고 나머지는 파사드에서 with로 덧씌움
		return MaterialResponse.ProjectMaterialState.from(unitKindCount, totalCount, usedCount);
	}
}
