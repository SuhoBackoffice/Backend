package baekgwa.suhoserver.domain.project.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.project.dto.ProjectResponse;
import baekgwa.suhoserver.domain.project.type.RailKind;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import baekgwa.suhoserver.model.branch.type.repository.BranchTypeRepository;
import baekgwa.suhoserver.model.project.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.repository.ProjectBranchRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.project.repository.ProjectRepository;
import baekgwa.suhoserver.model.project.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.project.straight.repository.ProjectStraightRepository;
import baekgwa.suhoserver.model.straight.type.entity.StraightTypeEntity;
import baekgwa.suhoserver.model.straight.type.repository.StraightTypeRepository;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import baekgwa.suhoserver.model.version.repository.VersionInfoRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.service
 * FileName    : ProjectService
 * Author      : Baekgwa
 * Date        : 2025-08-07
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-07     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final ProjectBranchRepository projectBranchRepository;
	private final BranchTypeRepository branchTypeRepository;
	private final StraightTypeRepository straightTypeRepository;
	private final VersionInfoRepository versionInfoRepository;
	private final ProjectStraightRepository projectStraightRepository;

	@Transactional
	public ProjectResponse.NewProjectDto createNewProject(ProjectRequest.PostNewProjectDto postNewProjectDto) {
		// 1. 입력값 유효성 검증
		if (postNewProjectDto.getStartDate() != null &&
			postNewProjectDto.getEndDate() != null &&
			!postNewProjectDto.getEndDate().isAfter(postNewProjectDto.getStartDate())) {
			throw new GlobalException(ErrorCode.PROJECT_END_AFTER_START_ERROR);
		}

		// 2. version info entity 조회 및 유효성 검증
		VersionInfoEntity findVersion = versionInfoRepository.findById(postNewProjectDto.getVersionId())
			.orElseThrow(
				() -> new GlobalException(ErrorCode.NOT_FOUND_VERSION));

		// 3. Entity 생성 및 저장
		ProjectEntity newProject = ProjectEntity.createNewProject(findVersion, postNewProjectDto.getName(),
			postNewProjectDto.getRegion(), postNewProjectDto.getStartDate(), postNewProjectDto.getEndDate());
		ProjectEntity savedProject = projectRepository.save(newProject);

		return new ProjectResponse.NewProjectDto(savedProject.getId());
	}

	@Transactional
	public ProjectResponse.NewProjectDto registerProjectBranch(
		List<ProjectRequest.PostProjectBranchInfo> postProjectBranchInfoList, Long projectId
	) {
		// 1. 프로젝트 조회
		ProjectEntity findProject = projectRepository.findById(projectId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. branch 정보 조회
		List<Long> branchIdList = postProjectBranchInfoList.stream()
			.map(ProjectRequest.PostProjectBranchInfo::getBranchTypeId)
			.distinct()
			.toList();
		List<BranchTypeEntity> findBranchTypeList = branchTypeRepository.findAllById(branchIdList);

		// 2-1. 추가 검증. 찾아온 branchType 이 id List 보다 작으면, 잘못된 id가 입력된 경우. (없는 것)
		if (branchIdList.size() > findBranchTypeList.size())
			throw new GlobalException(ErrorCode.NOT_FOUND_BRANCH_TYPE);

		// 2-2. branch 정보 stream 으로 map 처리
		Map<Long, BranchTypeEntity> findBranchTypeMap = findBranchTypeList.stream()
			.collect(Collectors.toMap(BranchTypeEntity::getId, Function.identity()));

		// 3. ProjectBranch Entity List 생성
		List<ProjectBranchEntity> newProjectBranchList = postProjectBranchInfoList.stream()
			.map(dto -> {
				BranchTypeEntity branchType = findBranchTypeMap.get(dto.getBranchTypeId());
				if (branchType == null) {
					throw new GlobalException(ErrorCode.NOT_FOUND_BRANCH_TYPE);
				}
				if (!findProject.getVersionInfoEntity().getId().equals(branchType.getVersionInfoEntity().getId())) {
					throw new GlobalException(ErrorCode.INVALID_VERSION_BRANCH);
				}
				return ProjectBranchEntity.createNewProjectBranch(findProject, branchType, dto.getQuantity());
			}).toList();

		// 4. 전체 저장
		projectBranchRepository.saveAll(newProjectBranchList);

		return new ProjectResponse.NewProjectDto(projectId);
	}

	@Transactional(readOnly = true)
	public ProjectResponse.ProjectInfo getProjectInfo(Long projectId) {
		// 1. Project 유효성 검증 및 Data 조회
		ProjectEntity findProject = projectRepository.findById(projectId).orElseThrow(
			() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. Project 의 Branch 정보 조회
		List<ProjectBranchEntity> findProjectBranchList = projectBranchRepository.findByProject(findProject);
		List<ProjectResponse.BranchInfo> branchInfoList = findProjectBranchList.stream()
			.map(ProjectResponse.BranchInfo::of)
			.toList();

		// todo: 3. Project 의 Strategy 정보 조회

		return ProjectResponse.ProjectInfo.of(findProject, branchInfoList);
	}

	@Transactional
	public void registerProjectStraight(
		List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList,
		Long projectId,
		RailKind railKind
	) {
		// 1. 프로젝트 정보 조회
		ProjectEntity findProject = projectRepository.findById(projectId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. straightTypeId 으로, 필요한 StraightType List 조회
		List<Long> straightTypeIdList = postProjectStraightInfoList.stream()
			.map(ProjectRequest.PostProjectStraightInfo::getStraightTypeId)
			.distinct()
			.toList();
		List<StraightTypeEntity> findStraightTypeList = straightTypeRepository.findAllById(straightTypeIdList);

		// 2-1. id 기반으로, 검색 가능하도록 Map 으로 convert
		Map<Long, StraightTypeEntity> findStraightTypeMap = findStraightTypeList
			.stream().collect(Collectors.toMap(StraightTypeEntity::getId, Function.identity()));

		// 3. ProjectStraightEntity List 생성 및 저장
		List<ProjectStraightEntity> projectStraightList = postProjectStraightInfoList.stream().map(
				dto -> {
					StraightTypeEntity findStraightType = findStraightTypeMap.get(dto.getStraightTypeId());
					if (findStraightType == null) {
						throw new GlobalException(ErrorCode.NOT_FOUND_STRAIGHT_TYPE);
					}
					return ProjectStraightEntity
						.createNewStraight(findProject, findStraightType, dto.getTotalQuantity(), railKind, dto.getLength());
				})
			.toList();
		projectStraightRepository.saveAll(projectStraightList);
	}
}
