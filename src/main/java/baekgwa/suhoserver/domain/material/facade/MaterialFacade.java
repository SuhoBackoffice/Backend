package baekgwa.suhoserver.domain.material.facade;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.branch.service.BranchReadService;
import baekgwa.suhoserver.domain.material.dto.MaterialResponseDto;
import baekgwa.suhoserver.domain.project.service.ProjectReadService;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.material.facade
 * FileName    : MaterialFacade
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
public class MaterialFacade {

	private final ProjectReadService projectReadService;
	private final BranchReadService branchReadService;

	@Transactional(readOnly = true)
	public List<MaterialResponseDto.MaterialInfo> getMaterialList(Long projectId, String keyword) {
		// 1. 프로젝트에 할당된, 분기 리스트 조회
		List<Long> findBranchTypeIdList = projectReadService.getBranchTypeIdList(projectId);

		// 2. 분기레일 자재 목록 조회
		return branchReadService.getAllBranchBomList(findBranchTypeIdList, keyword);
	}
}
