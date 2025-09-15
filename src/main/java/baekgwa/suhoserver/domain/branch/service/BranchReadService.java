package baekgwa.suhoserver.domain.branch.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
