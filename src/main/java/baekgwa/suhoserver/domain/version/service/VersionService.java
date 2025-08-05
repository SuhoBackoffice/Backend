package baekgwa.suhoserver.domain.version.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.version.dto.VersionRequest;
import baekgwa.suhoserver.domain.version.dto.VersionResponse;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import baekgwa.suhoserver.model.version.repository.VersionInfoRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.version.service
 * FileName    : VersionService
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
public class VersionService {

	private final VersionInfoRepository versionInfoRepository;

	@Transactional
	public void createNewVersion(VersionRequest.NewVersionDto newVersionDto) {
		// 1. 중복 확인
		if( versionInfoRepository.existsByName(newVersionDto.getVersionName()) ){
			throw new GlobalException(ErrorCode.DUPLICATE_VERSION_NAME);
		}

		// 2. 새로운 Entity 생성
		VersionInfoEntity newVersion = VersionInfoEntity.of(newVersionDto.getVersionName());

		// 3. 저장
		versionInfoRepository.save(newVersion);
	}

	@Transactional(readOnly = true)
	public List<VersionResponse.VersionListDto> getVersionList() {
		// 1. 리스트 조회
		return versionInfoRepository.findAll()
			.stream()
			.map(data -> VersionResponse.VersionListDto.of(data.getId(), data.getName()))
			.toList();
	}
}
