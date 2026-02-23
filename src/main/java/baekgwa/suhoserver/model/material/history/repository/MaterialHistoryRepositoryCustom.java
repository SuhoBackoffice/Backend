package baekgwa.suhoserver.model.material.history.repository;

import org.springframework.data.domain.Page;

import baekgwa.suhoserver.domain.material.dto.MaterialRequest;
import baekgwa.suhoserver.domain.material.dto.MaterialResponse;

/**
 * PackageName : baekgwa.suhoserver.model.material.history.repository
 * FileName    : MaterialHistoryRepositoryCustom
 * Author      : Baekgwa
 * Date        : 26. 2. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 23.     Baekgwa               Initial creation
 */
public interface MaterialHistoryRepositoryCustom {

	/**
	 * 프로젝트 자재 이력 페이징 조회
	 * @param projectId 프로젝트 PK
	 * @param dto 페이징 및 필터 조건
	 * @return 자재 이력 페이징 결과
	 */
	Page<MaterialResponse.MaterialHistoryInfo> searchHistoryList(Long projectId, MaterialRequest.GetMaterialHistory dto);
}
