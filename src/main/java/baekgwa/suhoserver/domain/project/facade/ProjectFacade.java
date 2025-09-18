package baekgwa.suhoserver.domain.project.facade;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.branch.service.BranchReadService;
import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.project.dto.ProjectResponse;
import baekgwa.suhoserver.domain.project.service.ProjectBomService;
import baekgwa.suhoserver.domain.project.service.ProjectReadService;
import baekgwa.suhoserver.domain.project.service.ProjectWriteService;
import baekgwa.suhoserver.domain.straight.service.StraightReadService;
import baekgwa.suhoserver.domain.straight.service.StraightWriteService;
import baekgwa.suhoserver.domain.version.service.VersionReadService;
import baekgwa.suhoserver.global.response.PageResponse;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import baekgwa.suhoserver.model.project.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.straight.info.entity.StraightInfoEntity;
import baekgwa.suhoserver.model.straight.type.entity.StraightTypeEntity;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.facade
 * FileName    : ProjectFacade
 * Author      : Baekgwa
 * Date        : 2025-09-15
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-15     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class ProjectFacade {

	private final VersionReadService versionReadService;

	private final ProjectReadService projectReadService;
	private final ProjectWriteService projectWriteService;
	private final ProjectBomService projectBomService;

	private final BranchReadService branchReadService;

	private final StraightReadService straightReadService;
	private final StraightWriteService straightWriteService;

	@Transactional
	public ProjectResponse.NewProjectDto createNewProject(ProjectRequest.PostNewProjectDto postNewProjectDto) {

		// 1. 버전 정보 조회
		VersionInfoEntity findVersion = versionReadService.getVersionInfoOrThrow(postNewProjectDto.getVersionId());

		// 2. 신규 프로젝트 생성 및 저장
		ProjectEntity savedProject = projectWriteService.createNewProjectOrThrow(postNewProjectDto, findVersion);

		// 3. 응답 객체 생성 및 반환
		return new ProjectResponse.NewProjectDto(savedProject.getId());
	}

	@Transactional(readOnly = true)
	public ProjectResponse.ProjectDetailInfo getProjectInfo(Long projectId) {
		// 1. 프로젝트 조회
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);

		// 2. 응답 객체 생성 및 반환
		return ProjectResponse.ProjectDetailInfo.of(findProject);
	}

	@Transactional
	public ProjectResponse.NewProjectDto registerProjectBranch(
		List<ProjectRequest.PostProjectBranchInfo> postProjectBranchInfoList, Long projectId
	) {
		// 1. 프로젝트 조회
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);

		// 2. Branch 정보 조회
		Set<Long> branchIdSet = postProjectBranchInfoList.stream()
			.map(ProjectRequest.PostProjectBranchInfo::getBranchTypeId)
			.collect(Collectors.toSet());
		Map<Long, BranchTypeEntity> findBranchTypeMap = branchReadService.getBranchTypeListOrThrow(branchIdSet);

		// 3. 신규 ProjectBranch 생성
		projectWriteService.registerProjectBranchOrThrow(postProjectBranchInfoList, findProject, findBranchTypeMap);

		return new ProjectResponse.NewProjectDto(projectId);
	}

	@Transactional
	public void registerProjectStraight(
		List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList, Long projectId
	) {
		// 1. 프로젝트 조회
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);

		// 2. 필요한 직선레일 타입정보 조회
		Set<Long> straightTypeIdList = postProjectStraightInfoList.stream()
			.map(ProjectRequest.PostProjectStraightInfo::getStraightTypeId)
			.collect(Collectors.toSet());
		Map<Long, StraightTypeEntity> findStraightTypeMap = straightReadService.getStraightTypeList(straightTypeIdList);

		// 2. 직선레일 홀 위치 및 LitzWire 정보 생성 및 저장
		Map<ProjectRequest.PostProjectStraightInfo, StraightInfoEntity> straightInfoMap =
			straightWriteService.registerNewStraightInfo(
				postProjectStraightInfoList,
				findProject.getVersionInfoEntity(),
				findStraightTypeMap
			);

		// 3. 신규 직선레일 생성 및 등록
		projectWriteService.registerProjectStraightOrThrow(postProjectStraightInfoList, findProject,
			findStraightTypeMap, straightInfoMap);
	}

	@Transactional(readOnly = true)
	public List<ProjectResponse.ProjectBranchInfo> getProjectBranchInfo(Long projectId) {
		// 1. 프로젝트 조회
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);

		// 2. 분기레일 정보 조회
		List<ProjectBranchEntity> findProjectBranchList = projectReadService.getProjectBranchInfoListOrThrow(
			findProject);

		// 3. DTO 변환 및 응답
		return findProjectBranchList.stream()
			.map(ProjectResponse.ProjectBranchInfo::of)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<ProjectResponse.ProjectStraightInfo> getProjectStraightInfo(Long projectId) {
		// 1. 프로젝트 조회
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);

		// 2. 직선레일 정보 조회
		List<ProjectStraightEntity> findProjectStraightList = projectReadService.getProjectStraightListOrThrow(
			findProject);

		// 3. DTO 변환 및 응답
		return findProjectStraightList.stream()
			.map(ProjectResponse.ProjectStraightInfo::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public PageResponse<ProjectResponse.ProjectInfo> getProjectInfoList(ProjectRequest.GetProjectInfo dto) {
		// 1. 버전 유효성 검증
		versionReadService.invalidVersionIdOrThrow(dto.getVersionId());

		// 2. 프로젝트 정보 조회 및 return
		return projectReadService.getProjectInfoListOrThrow(dto);
	}

	@Transactional
	public void deleteProjectStraight(Long projectStraightId) {
		// 1. 프로젝트에 직선레일 특정 삭제
		Long straightInfoId = projectWriteService.deleteProjectStraightOrThrow(projectStraightId);

		// 2. 직선레일 정보 삭제
		straightWriteService.deleteStraightInfoOrThrow(straightInfoId);
	}

	@Transactional
	public void patchProjectStraight(
		Long projectStraightId,
		ProjectRequest.PatchProjectStraightDto patchProjectStraightDto
	) {
		projectWriteService.patchProjectStraightOrThrow(projectStraightId, patchProjectStraightDto);
	}

	@Transactional
	public void deleteProjectBranch(Long projectBranchId) {
		projectWriteService.deleteProjectBranchOrThrow(projectBranchId);
	}

	@Transactional
	public void patchProjectBranch(Long projectBranchId, ProjectRequest.PatchProjectBranchDto patchProjectBranchDto) {
		projectWriteService.patchProjectBranchOrThrow(projectBranchId, patchProjectBranchDto);
	}

	@Transactional(readOnly = true)
	public ProjectResponse.ProjectQuantityList getProjectQuantityList(Long projectId) {
		// 1. 프로젝트 정보 조회
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);

		// 2. 프로젝트 물량 리스트 생성
		return projectBomService.getProjectQuantityList(findProject);
	}
}
