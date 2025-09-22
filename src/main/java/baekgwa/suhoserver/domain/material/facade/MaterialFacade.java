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
import baekgwa.suhoserver.model.project.branch.entity.ProjectBranchEntity;
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

	@Transactional(readOnly = true)
	public MaterialResponse.ProjectMaterialState getProjectMaterialState(Long projectId) {
		// 1. 프로젝트 정보 조회
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);

		// 2. 프로젝트에 할당된 분기레일 정보 조회
		List<ProjectBranchEntity> findProjectBranchList =
			projectReadService.getProjectBranchInfoListOrThrow(findProject);
		// 추후, 직선레일 관련된 BOM List 가 정비되고, 직선레일 또한 입/출고 관리를 진행한다면, 관련 내용 추가 필요

		// 3. 분기레일 BOM 목록 조회
		// 반환되는 자재는, 같은 것(도번)은 합쳐지고, 생산 목표 생산 수량과 생산 완료 수량에 영향을 받아 계산될 것
		MaterialResponse.ProjectMaterialState findMaterialState =
			branchReadService.getBranchBomList(findProjectBranchList);

		// 4. 현재 입고 된 자재를 기반으로, 내용 추가
		return materialReadService.getMaterialState(findMaterialState, findProject);
	}
}
