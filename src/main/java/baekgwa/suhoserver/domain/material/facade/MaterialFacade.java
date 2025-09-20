package baekgwa.suhoserver.domain.material.facade;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.branch.service.BranchReadService;
import baekgwa.suhoserver.domain.material.dto.MaterialRequest;
import baekgwa.suhoserver.domain.material.dto.MaterialResponse;
import baekgwa.suhoserver.domain.material.service.MaterialReadService;
import baekgwa.suhoserver.domain.material.service.MaterialWriteService;
import baekgwa.suhoserver.domain.material.type.MaterialSort;
import baekgwa.suhoserver.domain.project.service.ProjectReadService;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
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
	private final MaterialWriteService materialWriteService;
	private final MaterialReadService materialReadService;

	@Transactional(readOnly = true)
	public List<MaterialResponse.MaterialInfo> getMaterialList(Long projectId, String keyword) {
		// 1. 프로젝트에 할당된, 분기 리스트 조회
		List<Long> findBranchTypeIdList = projectReadService.getBranchTypeIdList(projectId);

		// 2. 분기레일 자재 목록 조회
		return branchReadService.getAllBranchBomList(findBranchTypeIdList, keyword);
	}

	@Transactional
	public void postMaterialInbound(Long projectId, List<MaterialRequest.PostMaterialInbound> postMaterialInboundList) {
		// 1. 프로젝트 조회
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);

		// 2. 신규 Material Inbound 추가
		materialWriteService.postMaterialInbound(findProject, postMaterialInboundList);
	}

	@Transactional(readOnly = true)
	public List<MaterialResponse.MaterialHistory> getMaterialHistoryList(
		Long projectId, String keyword, MaterialSort sort
	) {
		return materialReadService.getMaterialHistroyList(projectId, keyword, sort);
	}

	@Transactional(readOnly = true)
	public List<MaterialResponse.MaterialHistoryDetail> getMaterialHistoryDetailList(
		Long projectId, String keyword, LocalDate date
	) {
		return materialReadService.getMaterialHistoryDetail(projectId, keyword, date);
	}
}
