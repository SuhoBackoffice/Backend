package baekgwa.suhoserver.model.material.project.repository;

import java.util.List;

import org.springframework.data.domain.Sort;

import baekgwa.suhoserver.domain.material.type.MaterialStockSort;
import baekgwa.suhoserver.model.material.project.entity.ProjectMaterialStockEntity;

/**
 * PackageName : baekgwa.suhoserver.model.material.project.repository
 * FileName    : ProjectMaterialStockRepositoryCustom
 * Author      : Baekgwa
 * Date        : 2026-02-25
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-02-25     Baekgwa               Initial creation
 */
public interface ProjectMaterialStockRepositoryCustom {

	/**
	 * 프로젝트 자재 재고 목록 조회 (keyword 검색 + 정렬)
	 * @param projectId 프로젝트 PK
	 * @param keyword   materialCode 또는 itemName 검색어 (null/blank 시 전체 조회)
	 * @param sort      정렬 기준
	 * @param dir       정렬 방향
	 * @return 자재 재고 목록
	 */
	List<ProjectMaterialStockEntity> searchStockList(
		Long projectId,
		String keyword,
		MaterialStockSort sort,
		Sort.Direction dir
	);
}
