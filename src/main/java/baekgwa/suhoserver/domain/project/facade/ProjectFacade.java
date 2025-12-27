package baekgwa.suhoserver.domain.project.facade;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.branch.service.BranchReadService;
import baekgwa.suhoserver.domain.branch.service.BranchSerialWriteService;
import baekgwa.suhoserver.domain.material.service.MaterialReadService;
import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.project.dto.ProjectResponse;
import baekgwa.suhoserver.domain.project.service.ProjectBomService;
import baekgwa.suhoserver.domain.project.service.ProjectReadService;
import baekgwa.suhoserver.domain.project.service.ProjectWriteService;
import baekgwa.suhoserver.domain.straight.service.StraightReadService;
import baekgwa.suhoserver.domain.straight.service.StraightSerialWriteService;
import baekgwa.suhoserver.domain.straight.service.StraightWriteService;
import baekgwa.suhoserver.domain.version.service.VersionReadService;
import baekgwa.suhoserver.global.response.PageResponse;
import baekgwa.suhoserver.infra.history.event.ProjectBranchCreatedEvent;
import baekgwa.suhoserver.infra.history.event.ProjectBranchCreatedEventDto;
import baekgwa.suhoserver.infra.history.event.ProjectBranchDeletedEvent;
import baekgwa.suhoserver.infra.history.event.ProjectBranchUpdatedEvent;
import baekgwa.suhoserver.infra.history.event.ProjectStraightCreatedEvent;
import baekgwa.suhoserver.infra.history.event.ProjectStraightCreatedEventDto;
import baekgwa.suhoserver.infra.history.event.ProjectStraightDeletedEvent;
import baekgwa.suhoserver.infra.history.event.ProjectStraightUpdatedEvent;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
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

	private final MaterialReadService materialReadService;
	private final StraightSerialWriteService straightSerialWriteService;
	private final BranchSerialWriteService branchSerialWriteService;

	private final ApplicationEventPublisher applicationEventPublisher;

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
		List<ProjectRequest.PostProjectBranchInfo> postProjectBranchInfoList, Long projectId, Long userId
	) {
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);

		Set<Long> branchIdSet = postProjectBranchInfoList.stream()
			.map(ProjectRequest.PostProjectBranchInfo::getBranchTypeId)
			.collect(Collectors.toSet());
		Map<Long, BranchTypeEntity> findBranchTypeMap = branchReadService.getBranchTypeListOrThrow(branchIdSet);

		List<ProjectBranchEntity> saveProjectBranchList = projectWriteService.registerProjectBranchOrThrow(
			postProjectBranchInfoList, findProject, findBranchTypeMap);

		branchSerialWriteService.registerProjectBranchSerial(saveProjectBranchList);

		List<ProjectBranchCreatedEventDto> eventDtoList = saveProjectBranchList.stream().map(
				pb -> new ProjectBranchCreatedEventDto(
					pb.getId(),
					pb.getBranchType().getId(),
					pb.getTotalQuantity(),
					pb.getBranchType().getCode()))
			.toList();
		ProjectBranchCreatedEvent event =
			new ProjectBranchCreatedEvent(findProject.getId(), userId, eventDtoList);
		applicationEventPublisher.publishEvent(event);

		return new ProjectResponse.NewProjectDto(projectId);
	}

	@Transactional
	public void registerProjectStraight(
		List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList, Long projectId, Long userId
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
		List<ProjectStraightEntity> saveProjectStraightList = projectWriteService.registerProjectStraightOrThrow(
			postProjectStraightInfoList,
			findProject,
			findStraightTypeMap,
			straightInfoMap
		);

		// 4. 신규 등록된 직선레일 Serial 등록
		straightSerialWriteService.registerProjectStraightSerial(saveProjectStraightList);

		// 5. history 등록
		List<ProjectStraightCreatedEventDto> eventDtoList = saveProjectStraightList.stream()
			.map(ps -> new ProjectStraightCreatedEventDto(
				ps.getId(),
				ps.getLength(),
				ps.getIsLoopRail(),
				ps.getStraightType().getType(),
				ps.getTotalQuantity()
			))
			.toList();
		ProjectStraightCreatedEvent createdEvent =
			new ProjectStraightCreatedEvent(
				findProject.getId(),
				userId,
				eventDtoList
			);
		applicationEventPublisher.publishEvent(createdEvent);
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
	public void deleteProjectStraight(Long projectStraightId, Long userId) {
		ProjectStraightEntity findProjectStraight = projectReadService.getProjectStraightOrThrow(projectStraightId);

		projectWriteService.deleteProjectStraightOrThrow(findProjectStraight);
		straightWriteService.deleteStraightInfoOrThrow(findProjectStraight.getStraightInfo().getId());

		ProjectStraightDeletedEvent event = new ProjectStraightDeletedEvent(
			findProjectStraight.getProject().getId(),
			userId,
			findProjectStraight.getId(),
			findProjectStraight.getLength(),
			findProjectStraight.getIsLoopRail(),
			findProjectStraight.getStraightType().getType(),
			findProjectStraight.getTotalQuantity()
		);
		applicationEventPublisher.publishEvent(event);
	}

	@Transactional
	public void patchProjectStraight(
		Long projectStraightId,
		ProjectRequest.PatchProjectStraightDto patchProjectStraightDto,
		Long userId
	) {
		ProjectStraightEntity findStraight = projectReadService.getProjectStraightOrThrow(projectStraightId);
		Long oldQuantity = findStraight.getTotalQuantity();
		Long newQuantity = patchProjectStraightDto.getTotalQuantity();

		projectWriteService.patchProjectStraightOrThrow(findStraight, newQuantity);

		straightSerialWriteService.patchProjectStraightSerial(findStraight, oldQuantity, newQuantity);

		ProjectStraightUpdatedEvent event = new ProjectStraightUpdatedEvent(
			findStraight.getProject().getId(),
			userId,
			findStraight.getId(),
			findStraight.getLength(),
			findStraight.getIsLoopRail(),
			findStraight.getStraightType().getType(),
			oldQuantity,
			newQuantity
		);
		applicationEventPublisher.publishEvent(event);
	}

	@Transactional
	public void deleteProjectBranch(Long projectBranchId, Long userId) {
		ProjectBranchEntity findProjectBranch = projectReadService.getProjectBranchOrThrow(projectBranchId);

		projectWriteService.deleteProjectBranch(findProjectBranch);

		ProjectBranchDeletedEvent event = new ProjectBranchDeletedEvent(
			findProjectBranch.getProject().getId(),
			userId,
			findProjectBranch.getId(),
			findProjectBranch.getBranchType().getId(),
			findProjectBranch.getTotalQuantity(),
			findProjectBranch.getBranchType().getCode()
		);
		applicationEventPublisher.publishEvent(event);
	}

	@Transactional
	public void patchProjectBranch(Long projectBranchId, ProjectRequest.PatchProjectBranchDto request, Long userId) {
		ProjectBranchEntity findProjectBranch =
			projectReadService.getProjectBranchOrThrow(projectBranchId);

		Long oldQuantity = findProjectBranch.getTotalQuantity();
		Long newQuantity = request.getTotalQuantity();

		projectWriteService.patchProjectBranchOrThrow(findProjectBranch, request.getTotalQuantity());

		branchSerialWriteService.patchProjectBranchSerial(findProjectBranch, oldQuantity, newQuantity);

		ProjectBranchUpdatedEvent event = new ProjectBranchUpdatedEvent(
			findProjectBranch.getProject().getId(),
			userId,
			findProjectBranch.getId(),
			findProjectBranch.getBranchType().getId(),
			oldQuantity,
			newQuantity,
			findProjectBranch.getBranchType().getCode()
		);
		applicationEventPublisher.publishEvent(event);
	}

	@Transactional(readOnly = true)
	public ProjectResponse.ProjectQuantityList getProjectQuantityList(Long projectId) {
		// 1. 프로젝트 정보 조회
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);

		// 2. 프로젝트 물량 리스트 생성
		return projectBomService.getProjectQuantityList(findProject);
	}

	@Transactional(readOnly = true)
	public List<ProjectResponse.ProjectBranchCapacity> getProjectBranchCapacity(Long projectId) {
		// 1. 프로젝트에 입고된 자재 목록 조회 (Map)
		Map<String, Long> inboundedMaterialMap = materialReadService.getAllProjectMaterial(projectId);

		// 2. 프로젝트에 할당된 분기레일 종류 조회 (수량포함 목적 Entity)
		List<ProjectBranchEntity> projectBranchList = projectReadService.getBranchTypeList(projectId);

		// 3. 분기레일별로 생산 가능량 조회
		return branchReadService.getBranchCapacity(inboundedMaterialMap, projectBranchList);
	}

	@Transactional(readOnly = true)
	public List<ProjectResponse.OnGoingProjectInfo> getOnGoingProjectInfo() {
		return projectReadService.getOnGoingProjectInfoList();
	}
}
