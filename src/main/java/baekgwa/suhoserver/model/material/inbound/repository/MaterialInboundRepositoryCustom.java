package baekgwa.suhoserver.model.material.inbound.repository;

import java.util.List;

import baekgwa.suhoserver.domain.material.dto.MaterialResponse;
import baekgwa.suhoserver.domain.material.type.MaterialSort;

/**
 * PackageName : baekgwa.suhoserver.model.material.inbound.repository
 * FileName    : MaterialInboundRepositoryCustom
 * Author      : Baekgwa
 * Date        : 2025-09-20
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-20     Baekgwa               Initial creation
 */
public interface MaterialInboundRepositoryCustom {

	List<MaterialResponse.MaterialHistory> findByProjectAndKeyword(Long projectId, String keyword, MaterialSort sort);
}
