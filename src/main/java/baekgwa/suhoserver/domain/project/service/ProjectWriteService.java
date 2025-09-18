package baekgwa.suhoserver.domain.project.service;

import static java.lang.Boolean.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import baekgwa.suhoserver.model.project.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.repository.ProjectBranchRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.project.repository.ProjectRepository;
import baekgwa.suhoserver.model.project.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.project.straight.repository.ProjectStraightRepository;
import baekgwa.suhoserver.model.straight.info.entity.StraightInfoEntity;
import baekgwa.suhoserver.model.straight.type.entity.StraightTypeEntity;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.service
 * FileName    : ProjectWriteService
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
public class ProjectWriteService {

	private final ProjectRepository projectRepository;
	private final ProjectBranchRepository projectBranchRepository;
	private final ProjectStraightRepository projectStraightRepository;

	/**
	 * 신규 프로젝트 생성 메서드
	 * @param postNewProjectDto 프로젝트에 필요한 정보 dto
	 * @param findVersion 현재 프로젝트에 적용될 Version Ref
	 * @return 저장된 Project
	 */
	@Transactional
	public ProjectEntity createNewProjectOrThrow(
		ProjectRequest.PostNewProjectDto postNewProjectDto, VersionInfoEntity findVersion
	) {
		// 1. 입력값 유효성 검증
		//    시작일 == 종료일 은 허용
		if (postNewProjectDto.getStartDate() != null &&
			postNewProjectDto.getEndDate() != null &&
			postNewProjectDto.getEndDate().isBefore(postNewProjectDto.getStartDate())) {
			throw new GlobalException(ErrorCode.PROJECT_END_AFTER_START_ERROR);
		}

		// 2. 프로젝트 Entity 생성 및 저장
		ProjectEntity newProject = ProjectEntity.createNewProject(findVersion, postNewProjectDto.getName(),
			postNewProjectDto.getRegion(), postNewProjectDto.getStartDate(), postNewProjectDto.getEndDate());
		return projectRepository.save(newProject);
	}

	/**
	 * 프로젝트에 신규 분기레일 등록
	 * @param postProjectBranchInfoList 등록할 분기레일 정보
	 * @param findProject 프로젝트 정보 Entity
	 * @param findBranchTypeMap 분기레일 정보 Map <PK, Entity>
	 */
	@Transactional
	public void registerProjectBranchOrThrow(
		List<ProjectRequest.PostProjectBranchInfo> postProjectBranchInfoList,
		ProjectEntity findProject,
		Map<Long, BranchTypeEntity> findBranchTypeMap
	) {
		// 1. 현재 프로젝트에 등록된 분기레일 코드 조회 (중복 확인용)
		List<String> existBranchCode = projectBranchRepository.findAllByBranchTypeIdIn(
				findBranchTypeMap.keySet().stream().toList())
			.stream().map(data -> data.getBranchType().getCode()).toList();

		// 2. 프로젝트 분기레일 Entity List 생성
		List<ProjectBranchEntity> newProjectBranchList = postProjectBranchInfoList.stream()
			.map(dto -> {
				BranchTypeEntity branchType = findBranchTypeMap.get(dto.getBranchTypeId());
				if (branchType == null) {
					throw new GlobalException(ErrorCode.NOT_FOUND_BRANCH_TYPE);
				}
				if (!findProject.getVersionInfoEntity().getId().equals(branchType.getVersionInfoEntity().getId())) {
					throw new GlobalException(ErrorCode.INVALID_VERSION_BRANCH);
				}
				if (existBranchCode.contains(branchType.getCode())) {
					throw new GlobalException(ErrorCode.ALREADY_EXIST_PROJECT_BRANCH_DATA);
				}
				return ProjectBranchEntity.createNewProjectBranch(findProject, branchType, dto.getQuantity());
			}).toList();

		// 3. 프로젝트 분기레일 등록
		projectBranchRepository.saveAll(newProjectBranchList);
	}

	/**
	 * 프로젝트에 신규 직선레일 등록
	 * @param postProjectStraightInfoList 등록할 직선레일 정보
	 * @param findProject 프로젝트 정보
	 * @param findStraightTypeMap 직선레일 타입 정보 Map<PK, Entity>
	 * @param straightInfoMap 직선레일 정보 [가공 위치, LitzWire 6개] 를 담은 Map
	 */
	@Transactional
	public void registerProjectStraightOrThrow(
		List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList,
		ProjectEntity findProject,
		Map<Long, StraightTypeEntity> findStraightTypeMap,
		Map<ProjectRequest.PostProjectStraightInfo, StraightInfoEntity> straightInfoMap
	) {
		// 1. 입력 데이터 중복 검증
		// db에 이미 있거나, 중복된 요청이 오는 경우 [3600A, 3600A 2번 요청] 필터링
		validateDuplicationStraight(postProjectStraightInfoList, findProject);

		// 2. 프로젝트 직선레일 List 생성
		List<ProjectStraightEntity> newProjectStraightList = postProjectStraightInfoList.stream().map(
				dto -> {
					StraightTypeEntity straightType = findStraightTypeMap.get(dto.getStraightTypeId());
					if (straightType == null) {
						throw new GlobalException(ErrorCode.NOT_FOUND_STRAIGHT_TYPE);
					}
					if (!Objects.equals(straightType.getIsLoopRail(), dto.getIsLoopRail())) {
						if (TRUE.equals(dto.getIsLoopRail())) {
							throw new GlobalException(ErrorCode.NOT_MATCH_STRAIGHT_LOOP_TYPE);
						} else {
							throw new GlobalException(ErrorCode.NOT_MATCH_STRAIGHT_NORMAL_TYPE);
						}
					}

					StraightInfoEntity findStraightInfo = straightInfoMap.get(dto);

					return ProjectStraightEntity.createNewStraight(
						findProject,
						straightType,
						dto.getTotalQuantity(),
						dto.getIsLoopRail(),
						dto.getLength(),
						findStraightInfo);
				})
			.toList();

		// 전체 저장
		projectStraightRepository.saveAll(newProjectStraightList);
	}

	/**

	 */
	/**
	 * 프로젝트에 할당된 특정 직선레일 할당 해제
	 * @param projectStraightId 프로젝트에 할당된 직선레일 PK
	 * @return 추가로 삭제할 Straight Info PK
	 */
	@Transactional
	public Long deleteProjectStraightOrThrow(Long projectStraightId) {
		// 1. 삭제할 ProjectStraight Entity 조회
		ProjectStraightEntity findProjectStraight = projectStraightRepository.findById(projectStraightId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_PROJECT_STRAIGHT));

		// 2. straightInfoId 사전 추출
		Long straightInfoId = findProjectStraight.getStraightInfo().getId();

		// 3. 삭제 진행
		projectStraightRepository.delete(findProjectStraight);

		return straightInfoId;
	}

	/**
	 * 프로젝트에 할당된 직선레일 수정
	 * @param projectStraightId 프로젝트에 할당된 직선레일 PK
	 * @param dto 수정할 data
	 */
	@Transactional
	public void patchProjectStraightOrThrow(
		Long projectStraightId,
		ProjectRequest.PatchProjectStraightDto dto
	) {
		// 1. projectStraight Entity 조회
		ProjectStraightEntity findProjectStraight = projectStraightRepository.findById(projectStraightId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_PROJECT_STRAIGHT));

		// 2. 업데이트 처리
		findProjectStraight.patchProjectStraight(dto.getTotalQuantity());
	}

	/**
	 * 프로젝트에 할당된 분기레일 삭제
	 * @param projectBranchId 프로젝트에 할당된 분기레일 PK
	 */
	@Transactional
	public void deleteProjectBranchOrThrow(Long projectBranchId) {
		// 1. 유효성 확인
		if (!projectBranchRepository.existsById(projectBranchId)) {
			throw new GlobalException(ErrorCode.NOT_EXIST_PROJECT_BRANCH);
		}

		// 2. 삭제 진행
		projectBranchRepository.deleteById(projectBranchId);
	}

	/**
	 * 프로젝트에 할당된 분기레일 수정
	 * @param projectBranchId 프로젝트에 할당된 분기레일 PK
	 * @param dto 수정할 data
	 */
	@Transactional
	public void patchProjectBranchOrThrow(Long projectBranchId, ProjectRequest.PatchProjectBranchDto dto) {
		// 1. projectBranch Entity 조회
		ProjectBranchEntity findProjectBranch = projectBranchRepository.findById(projectBranchId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_PROJECT_BRANCH));

		// 2. 업데이트 처리
		findProjectBranch.patchProjectBranch(dto.getTotalQuantity());
	}

	private void validateDuplicationStraight(
		List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList,
		ProjectEntity findProject
	) {
		// 1-0. 요청 내부 중복 차단용 record
		record StraightKey(Long length, Long straightTypeId) {
		}

		Set<StraightKey> requestKeySet = new HashSet<>();
		for (ProjectRequest.PostProjectStraightInfo dto : postProjectStraightInfoList) {
			StraightKey key = new StraightKey(dto.getLength(), dto.getStraightTypeId());
			if (!requestKeySet.add(key)) {
				throw new GlobalException(ErrorCode.INVALID_PROJECT_STRAIGHT_REGISTER_DATA_DUPLICATION);
			}
		}

		// 1-1. DB 중복 차단: 해당 프로젝트의 기존 (length, straightTypeId)와 교집합 확인
		List<ProjectStraightEntity> existing = projectStraightRepository.findByProject(findProject);
		Set<StraightKey> existingKeySet = existing.stream()
			.map(e -> new StraightKey(e.getLength(), e.getStraightType().getId()))
			.collect(Collectors.toSet());

		for (StraightKey k : requestKeySet) {
			if (existingKeySet.contains(k)) {
				throw new GlobalException(ErrorCode.ALREADY_EXIST_PROJECT_STRAIGHT_DATA);
			}
		}
	}
}
