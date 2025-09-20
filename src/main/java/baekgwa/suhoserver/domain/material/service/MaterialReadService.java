package baekgwa.suhoserver.domain.material.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.material.dto.MaterialResponse;
import baekgwa.suhoserver.domain.material.type.MaterialSort;
import baekgwa.suhoserver.model.material.inbound.repository.MaterialInboundRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.material.service
 * FileName    : MaterialReadService
 * Author      : Baekgwa
 * Date        : 2025-09-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-19     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class MaterialReadService {

	private final MaterialInboundRepository materialInboundRepository;

	@Transactional(readOnly = true)
	public List<MaterialResponse.MaterialHistory> getMaterialHistroyList(
		Long projectId, String keyword, MaterialSort sort
	) {
		// 1. keyword 에 매칭되는 모든 material Info 조회
		return materialInboundRepository.findByProjectAndKeyword(projectId, keyword, sort);
	}
}
