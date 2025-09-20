package baekgwa.suhoserver.domain.branch.service;

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

		if(findBranchBomList.isEmpty()) {
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

		if(branchIdSet.size() != findBranchTypeList.size()) {
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
		List<BranchBomEntity> findBranchBomList = branchBomRepository.searchBranchBomList(findBranchTypeIdList, keyword);

		// 2. DTO 응답
		return findBranchBomList.stream()
			.map(MaterialResponse.MaterialInfo::of)
			.toList();
	}
}
