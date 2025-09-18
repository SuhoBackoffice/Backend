package baekgwa.suhoserver.domain.version.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.version.dto.VersionResponse;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import baekgwa.suhoserver.model.version.repository.VersionInfoRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.version.service
 * FileName    : VersionReadService
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
public class VersionReadService {

	private final VersionInfoRepository versionInfoRepository;

	/**
	 * 현존하는 모든 version 데이터 조회
	 * @return version List
	 */
	@Transactional(readOnly = true)
	public List<VersionResponse.VersionListDto> getVersionList() {
		return versionInfoRepository.findAll()
			.stream()
			.map(data -> VersionResponse.VersionListDto.of(data.getId(), data.getName()))
			.toList();
	}

	/**
	 * VersionId로, Version Entity 조회
	 * 없을 경우, throw GlobalException(ErrorCode.NOT_FOUND_VERSION)
	 * @param versionInfoId 버전 PK
	 * @return 조회된 VersionInfoEntity
	 */
	@Transactional(readOnly = true)
	public VersionInfoEntity getVersionInfoOrThrow(Long versionInfoId) {
		return versionInfoRepository.findById(versionInfoId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_VERSION));
	}

	/**
	 * 유효한 versionId 인지 검증하는 메서드
	 * 유효하지 않은 versionId 라면, Exception 발생
	 * @param versionId 버전 PK
	 */
	public void invalidVersionIdOrThrow(Long versionId) {
		if (versionId != null && !versionInfoRepository.existsById(versionId)) {
			throw new GlobalException(ErrorCode.NOT_FOUND_VERSION);
		}
	}
}
