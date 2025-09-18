package baekgwa.suhoserver.domain.version.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.version.dto.VersionRequest;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import baekgwa.suhoserver.model.version.repository.VersionInfoRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.version.service
 * FileName    : VersionWriteService
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
public class VersionWriteService {

	private final VersionInfoRepository versionInfoRepository;

	@Transactional
	public void createNewVersion(VersionRequest.NewVersionDto newVersionDto) {
		// 1. 중복 확인
		if( versionInfoRepository.existsByName(newVersionDto.getVersionName()) ){
			throw new GlobalException(ErrorCode.DUPLICATE_VERSION_NAME);
		}

		// 2. 새로운 Entity 생성
		VersionInfoEntity newVersion = VersionInfoEntity.of(newVersionDto.getVersionName(), newVersionDto.getLoopLitzWire());

		// 3. 저장
		versionInfoRepository.save(newVersion);
	}
}
