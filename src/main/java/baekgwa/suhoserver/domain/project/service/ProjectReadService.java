package baekgwa.suhoserver.domain.project.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.project.dto.ProjectResponse;
import baekgwa.suhoserver.domain.worker.dto.WorkReportRequest;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.global.response.PageResponse;
import baekgwa.suhoserver.model.project.ProductProductionState;
import baekgwa.suhoserver.model.project.ProductSerialState;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.branch.repository.ProjectBranchRepository;
import baekgwa.suhoserver.model.project.branch.serial.entity.ProjectBranchSerialEntity;
import baekgwa.suhoserver.model.project.branch.serial.repository.ProjectBranchSerialRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.project.repository.ProjectRepository;
import baekgwa.suhoserver.model.project.straight.serial.entity.ProjectStraightSerialEntity;
import baekgwa.suhoserver.model.project.straight.serial.repository.ProjectStraightSerialRepository;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.project.straight.straight.repository.ProjectStraightRepository;
import baekgwa.suhoserver.model.work.report.branch.repository.WorkReportBranchSerialRepository;
import baekgwa.suhoserver.model.work.report.straight.repository.WorkReportStraightSerialRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.service
 * FileName    : ProjectReadService
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
public class ProjectReadService {

	private final ProjectRepository projectRepository;
	private final ProjectBranchRepository projectBranchRepository;
	private final ProjectStraightRepository projectStraightRepository;
	private final ProjectStraightSerialRepository projectStraightSerialRepository;
	private final ProjectBranchSerialRepository projectBranchSerialRepository;
	private final WorkReportStraightSerialRepository workReportStraightSerialRepository;
	private final WorkReportBranchSerialRepository workReportBranchSerialRepository;

	/**
	 * projectId 로, 프로젝트 정보 조회
	 * @param projectId 프로젝트 PK
	 * @return 찾은 프로젝트 Entity
	 */
	@Transactional(readOnly = true)
	public ProjectEntity getProjectOrThrow(Long projectId) {
		return projectRepository.findById(projectId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));
	}

	/**
	 * 프로젝트에 저장된 분기레일 정보 조회
	 * @param project 프로젝트 Entity
	 * @return 분기레일 List
	 */
	@Transactional(readOnly = true)
	public List<ProjectBranchEntity> getProjectBranchInfoListOrThrow(ProjectEntity project) {
		return projectBranchRepository.findByProject(project);
	}

	/**
	 * 프로젝트에 저장된 직선레일 정보 조회
	 * @param project 프로젝트 Entity
	 * @return 직선레일 List
	 */
	@Transactional(readOnly = true)
	public List<ProjectStraightEntity> getProjectStraightListOrThrow(ProjectEntity project) {
		return projectStraightRepository.findByProject(project);
	}

	/**
	 * 프로젝트 페이징 조회
	 * @param dto 페이징 정보
	 * @return 찾은 프로젝트 페이징 리스트
	 */
	@Transactional(readOnly = true)
	public PageResponse<ProjectResponse.ProjectInfo> getProjectInfoListOrThrow(ProjectRequest.GetProjectInfo dto) {
		// 1. 페이지네이션 파라미터 유효성 검증
		if (dto.getPage() < 0 || dto.getSize() < 1) {
			throw new GlobalException(ErrorCode.INVALID_PAGINATION_PARAMETER);
		}

		// 2. StartDate, EndDate 검증
		if (dto.getStartDate() != null &&
			dto.getEndDate() != null &&
			!dto.getEndDate().isAfter(dto.getStartDate())) {
			throw new GlobalException(ErrorCode.PROJECT_END_AFTER_START_ERROR);
		}

		// 3. list 조회
		Page<ProjectResponse.ProjectInfo> findData = projectRepository.searchProjectList(dto);

		return PageResponse.of(findData);
	}

	/**
	 * 프로젝트에 할당된 분기레일의 종류 ID List 조회
	 * @param projectId 프로젝트 PK
	 * @return branchTypeIdList
	 */
	@Transactional(readOnly = true)
	public List<Long> getBranchTypeIdList(Long projectId) {
		return projectBranchRepository.findIdListByProjectId(projectId);
	}

	/**
	 * 프로젝트에 할당된 분기레일 종류 List 조회
	 * @param projectId 프로젝트 PK
	 * @return List<ProjectBranchEntity>
	 */
	@Transactional(readOnly = true)
	public List<ProjectBranchEntity> getBranchTypeList(Long projectId) {
		return projectBranchRepository.findByProjectId(projectId);
	}

	@Transactional(readOnly = true)
	public List<ProjectResponse.OnGoingProjectInfo> getOnGoingProjectInfoList() {
		return projectRepository.findOnGoingProjectList()
			.stream().map(ProjectResponse.OnGoingProjectInfo::of).toList();
	}

	/**
	 * 특정 프로젝트에 할당된 직선레일의 Entity 를 반환
	 * @param projectStraightId pk
	 * @return find ProjectStraightEntity
	 */
	@Transactional(readOnly = true)
	public ProjectStraightEntity getProjectStraightOrThrow(Long projectStraightId) {
		return projectStraightRepository.findById(projectStraightId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_PROJECT_STRAIGHT));
	}

	/**
	 * 특정 프로젝트에 할당된 직선레일의 Entity 를 반환
	 * @param projectBranchId pk
	 * @return find ProjectBranchEntity or Throw Exception
	 */
	@Transactional(readOnly = true)
	public ProjectBranchEntity getProjectBranchOrThrow(Long projectBranchId) {
		return projectBranchRepository.findById(projectBranchId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_PROJECT_BRANCH));
	}

	/**
	 * 아직 생산이 완료되지 않은 Project Straight Entity List 반환
	 * @param findProject 검색 프로젝트 Entity
	 * @return find ProjectStraightEntity List
	 */
	@Transactional(readOnly = true)
	public List<ProjectStraightEntity> getUnCompletedProjectStraightList(ProjectEntity findProject) {
		return projectStraightRepository.findUnCompletedByProject(findProject);
	}

	/**
	 * 아직 생산이 완료되지 않은 Project Branch Entity List 반환
	 * @param findProject 검색 프로젝트 Entity
	 * @return find ProjectBranchEntity List
	 */
	public List<ProjectBranchEntity> getUnCompletedProjectBranchList(ProjectEntity findProject) {
		return projectBranchRepository.findUnCompletedByProject(findProject);
	}

	/**
	 * 활성화 된 직선레일 시리얼 번호 목록 반환
	 * @param straightId 프로젝트에 할당된 직선레일 PK
	 * @return find List<ProjectStraightSerialEntity>
	 */
	@Transactional(readOnly = true)
	public List<ProjectStraightSerialEntity> getProjectStraightSerialList(Long straightId) {
		return projectStraightSerialRepository.findProjectStraightSerialList(straightId, ProductSerialState.ACTIVE);
	}

	@Transactional(readOnly = true)
	public Map<Long, ProjectStraightEntity> getProjectStraightMap(
		WorkReportRequest.PostNewWorkReport request
	) {
		if (request.getStraightReportList().isEmpty()) {
			return Map.of();
		}

		List<Long> psIdList = request.getStraightReportList().stream()
			.map(WorkReportRequest.PostNewWorkStraightReport::getProjectStraightId)
			.toList();

		List<ProjectStraightEntity> findProjectStraightList =
			projectStraightRepository.findAllById(psIdList);

		if (findProjectStraightList.size() != request.getStraightReportList().size()) {
			throw new GlobalException(ErrorCode.NOT_REGISTERED_PROJECT_STRAIGHT);
		}

		return findProjectStraightList.stream()
			.collect(Collectors.toMap(
				ProjectStraightEntity::getId,
				Function.identity())
			);
	}

	/**
	 * 프로젝트에 할당된 분기레일 목록을 조회
	 * @param request
	 * @return Map key: ProjectBranch PK, value: ProjectBranch Entity
	 */
	@Transactional(readOnly = true)
	public Map<Long, ProjectBranchEntity> getProjectBranchMap(
		WorkReportRequest.PostNewWorkReport request
	) {
		if (request.getBranchReportList().isEmpty()) {
			return Map.of();
		}

		List<Long> projectBranchIdList = request.getBranchReportList()
			.stream()
			.map(WorkReportRequest.PostNewWorkBranchReport::getProjectBranchId)
			.toList();

		List<ProjectBranchEntity> findProjectBranchList =
			projectBranchRepository.findAllById(projectBranchIdList);

		if (findProjectBranchList.size() != request.getBranchReportList().size()) {
			throw new GlobalException(ErrorCode.NOT_REGISTERED_PROJECT_BRANCH);
		}

		return findProjectBranchList.stream()
			.collect(Collectors.toMap(
				ProjectBranchEntity::getId,
				Function.identity()
			));
	}

	/**
	 * 직선레일 시리얼 ID 를 기반으로 serial 이름을 찾아오는 메서드
	 * @param request
	 * @return Map<프로젝트에 할당된 직선레일 PK, Map<직선레일 시리얼 PK, 직선레일 시리얼 이름>>
	 */
	@Transactional(readOnly = true)
	public Map<Long, Map<Long, String>> getStraightSerialSnapshot(
		WorkReportRequest.PostNewWorkReport request
	) {
		if (request.getStraightReportList().isEmpty()) {
			return Map.of();
		}

		List<Long> allSerialIds = request.getStraightReportList().stream()
			.flatMap(r -> r.getProjectStraightSerialIdList().stream())
			.distinct()
			.toList();

		List<ProjectStraightSerialEntity> serialEntities =
			projectStraightSerialRepository.findAllByIdIn(allSerialIds);

		if (serialEntities.size() != allSerialIds.size()) {
			throw new GlobalException(ErrorCode.INVALID_STRAIGHT_SERIAL);
		}

		Map<Long, Set<Long>> dbSerialMap =
			serialEntities.stream()
				.collect(Collectors.groupingBy(
					s -> s.getProjectStraight().getId(),
					Collectors.mapping(ProjectStraightSerialEntity::getId, Collectors.toSet())
				));

		for (WorkReportRequest.PostNewWorkStraightReport straight : request.getStraightReportList()) {

			Set<Long> requestedSerialIds =
				new HashSet<>(straight.getProjectStraightSerialIdList());

			Set<Long> actualSerialIds =
				dbSerialMap.getOrDefault(straight.getProjectStraightId(), Set.of());

			if (!actualSerialIds.containsAll(requestedSerialIds)) {
				throw new GlobalException(ErrorCode.INVALID_STRAIGHT_SERIAL);
			}

			if (requestedSerialIds.size() != straight.getProductionQuantity()) {
				throw new GlobalException(ErrorCode.NOT_MATCH_STRAIGHT_PRODUCTION_SERIAL_COUNT);
			}
		}

		if (serialEntities.stream()
			.anyMatch(s -> s.getState() != ProductSerialState.ACTIVE)) {
			throw new GlobalException(ErrorCode.INACTIVE_STRAIGHT_SERIAL);
		}

		if (serialEntities.stream()
			.anyMatch(s -> s.getProductionState() != ProductProductionState.NOT_PRODUCED)) {
			throw new GlobalException(ErrorCode.ALREADY_PRODUCED_STRAIGHT_SERIAL);
		}

		List<Long> serialIdList = serialEntities.stream()
			.map(ProjectStraightSerialEntity::getId)
			.toList();

		boolean alreadyUsed =
			workReportStraightSerialRepository
				.existsByProjectStraightSerialIdIn(serialIdList);

		if (alreadyUsed) {
			throw new GlobalException(ErrorCode.ALREADY_USED_STRAIGHT_SERIAL);
		}

		Map<Long, String> serialMap =
			serialEntities.stream()
				.collect(Collectors.toMap(
					ProjectStraightSerialEntity::getId,
					ProjectStraightSerialEntity::getSerial
				));

		Map<Long, Map<Long, String>> result = new HashMap<>();

		for (WorkReportRequest.PostNewWorkStraightReport straight : request.getStraightReportList()) {

			Map<Long, String> map = new HashMap<>();

			for (Long serialId : straight.getProjectStraightSerialIdList()) {
				map.put(serialId, serialMap.get(serialId));
			}

			result.put(straight.getProjectStraightId(), map);
		}

		return result;
	}

	/**
	 * 분기레일 시리얼 ID 를 기반으로 serial 이름 찾아오는 메서드
	 * @param request
	 * @return Map<프로젝트에 할당된 분기레일 PK, Map<분기레일 시리얼 PK, 분기레일 시리얼 이름>>
	 */
	@Transactional(readOnly = true)
	public Map<Long, Map<Long, String>> getBranchSerialSnapshot(
		WorkReportRequest.PostNewWorkReport request
	) {
		if (request.getBranchReportList().isEmpty()) {
			return Map.of();
		}

		List<Long> allSerialIds = request.getBranchReportList().stream()
			.flatMap(b -> b.getProjectBranchSerialIdList().stream())
			.distinct()
			.toList();

		List<ProjectBranchSerialEntity> serialEntities =
			projectBranchSerialRepository.findAllById(allSerialIds);

		if (serialEntities.size() != allSerialIds.size()) {
			throw new GlobalException(ErrorCode.INVALID_BRANCH_SERIAL);
		}

		Map<Long, Set<Long>> dbSerialMap =
			serialEntities.stream()
				.collect(Collectors.groupingBy(
					s -> s.getProjectBranch().getId(),
					Collectors.mapping(ProjectBranchSerialEntity::getId, Collectors.toSet())
				));

		for (WorkReportRequest.PostNewWorkBranchReport branch : request.getBranchReportList()) {

			Set<Long> requestedSerialIds =
				new HashSet<>(branch.getProjectBranchSerialIdList());

			Set<Long> actualSerialIds =
				dbSerialMap.getOrDefault(branch.getProjectBranchId(), Set.of());

			if (!actualSerialIds.containsAll(requestedSerialIds)) {
				throw new GlobalException(ErrorCode.INVALID_BRANCH_SERIAL);
			}

			if (requestedSerialIds.size() != branch.getProductionQuantity()) {
				throw new GlobalException(ErrorCode.NOT_MATCH_BRANCH_PRODUCTION_SERIAL_COUNT);
			}
		}

		if (serialEntities.stream()
			.anyMatch(s -> s.getState() != ProductSerialState.ACTIVE)) {
			throw new GlobalException(ErrorCode.INACTIVE_BRANCH_SERIAL);
		}

		if (serialEntities.stream()
			.anyMatch(s -> s.getProductionState() != ProductProductionState.NOT_PRODUCED)) {
			throw new GlobalException(ErrorCode.ALREADY_PRODUCED_BRANCH_SERIAL);
		}

		List<Long> serialIdList = serialEntities.stream()
			.map(ProjectBranchSerialEntity::getId)
			.toList();

		boolean alreadyUsed =
			workReportBranchSerialRepository
				.existsByProjectBranchSerialIdIn(serialIdList);

		if (alreadyUsed) {
			throw new GlobalException(ErrorCode.ALREADY_USED_BRANCH_SERIAL);
		}

		Map<Long, String> serialMap =
			serialEntities.stream()
				.collect(Collectors.toMap(
					ProjectBranchSerialEntity::getId,
					ProjectBranchSerialEntity::getSerial
				));

		Map<Long, Map<Long, String>> result = new HashMap<>();

		for (WorkReportRequest.PostNewWorkBranchReport branch : request.getBranchReportList()) {

			Map<Long, String> map = new HashMap<>();

			for (Long serialId : branch.getProjectBranchSerialIdList()) {
				map.put(serialId, serialMap.get(serialId));
			}

			result.put(branch.getProjectBranchId(), map);
		}

		return result;
	}
}
