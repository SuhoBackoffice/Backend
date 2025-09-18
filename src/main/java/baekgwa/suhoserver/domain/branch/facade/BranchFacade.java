package baekgwa.suhoserver.domain.branch.facade;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import baekgwa.suhoserver.domain.branch.dto.BranchResponse;
import baekgwa.suhoserver.domain.branch.service.BranchReadService;
import baekgwa.suhoserver.domain.branch.service.BranchWriteService;
import baekgwa.suhoserver.domain.version.service.VersionReadService;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.domain.branch.service
 * FileName    : BranchFacade
 * Author      : Baekgwa
 * Date        : 2025-08-05
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-05     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BranchFacade {
	private final BranchReadService branchReadService;
	private final BranchWriteService branchWriteService;
	private final VersionReadService versionReadService;

	@Transactional
	public BranchResponse.PostNewBranchBom createNewBranchBom(
		String branchCode, Long versionInfoId, MultipartFile file, String imageUrl
	) {
		// 1. 버전 유효성 검증 및, Entity 조회
		VersionInfoEntity findVersionInfo = versionReadService.getVersionInfoOrThrow(versionInfoId);

		// 1-1. 생성 가능 확인
		if(branchReadService.isTodayUpdated(findVersionInfo, branchCode)) {
			throw new GlobalException(ErrorCode.ALREADY_UPLOADED_COMPLETE_BRANCH_BOM);
		}

		// 2. Branch Type 신규 생성 및 저장
		BranchTypeEntity savedBranchType =
			branchWriteService.saveNewBranchType(file, findVersionInfo, branchCode, imageUrl);

		// 3. Branch Bom List 신규 생성 및 저장
		branchWriteService.saveNewBranchBom(savedBranchType, file);

		return new BranchResponse.PostNewBranchBom(savedBranchType.getId());
	}

	@Transactional(readOnly = true)
	public BranchResponse.BranchInfoDto getLatestVersionBranchBom(String branchCode, Long versionInfoId) {
		// 1. 분기레일 타입 조회
		BranchTypeEntity findLatestBranchType = branchReadService.getLatestBranchTypeOrThrow(versionInfoId, branchCode);

		// 2. 분기레일 BOM List 조회
		List<BranchBomEntity> findBranchBomList = branchReadService.getBranchBomListOrThrow(findLatestBranchType);

		// 3. DTO Convert
		List<BranchResponse.BranchDetailInfoDto> branchDetailInfoDtoList =
			findBranchBomList.stream().map(BranchResponse.BranchDetailInfoDto::of).toList();

		return BranchResponse.BranchInfoDto.from(findLatestBranchType, branchDetailInfoDtoList);
	}

	@Transactional(readOnly = true)
	public List<BranchResponse.BranchDetailInfoDto> getBranchBomList(Long branchTypeId) {
		// 1. branchType Entity 조회
		BranchTypeEntity findBranchType = branchReadService.getBranchTypeOrThrow(branchTypeId);

		// 2. 분기레일 BOM List 조회
		List<BranchBomEntity> findBranchBomList = branchReadService.getBranchBomListOrThrow(findBranchType);

		// 3. 찾은 BranchBomEntity 로, 응답 객체 생성
		return findBranchBomList.stream()
			.map(BranchResponse.BranchDetailInfoDto::of)
			.toList();
	}
}
