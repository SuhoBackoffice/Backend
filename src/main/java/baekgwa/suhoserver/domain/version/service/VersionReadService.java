package baekgwa.suhoserver.domain.version.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.version.dto.VersionResponse;
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

	@Transactional(readOnly = true)
	public List<VersionResponse.VersionListDto> getVersionList() {
		return versionInfoRepository.findAll()
			.stream()
			.map(data -> VersionResponse.VersionListDto.of(data.getId(), data.getName()))
			.toList();
	}
}
