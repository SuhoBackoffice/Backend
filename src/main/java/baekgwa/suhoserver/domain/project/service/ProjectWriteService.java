package baekgwa.suhoserver.domain.project.service;

import static java.lang.Boolean.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import baekgwa.suhoserver.model.project.ProductProductionState;
import baekgwa.suhoserver.model.project.ProductSerialState;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.branch.repository.ProjectBranchRepository;
import baekgwa.suhoserver.model.project.branch.serial.entity.ProjectBranchSerialEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.project.repository.ProjectRepository;
import baekgwa.suhoserver.model.project.straight.serial.entity.ProjectStraightSerialEntity;
import baekgwa.suhoserver.model.project.straight.serial.repository.ProjectStraightSerialRepository;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.project.straight.straight.repository.ProjectStraightRepository;
import baekgwa.suhoserver.model.straight.type.entity.StraightTypeEntity;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import baekgwa.suhoserver.model.work.report.branch.entity.WorkReportBranchEntity;
import baekgwa.suhoserver.model.work.report.branch.entity.WorkReportBranchSerialEntity;
import baekgwa.suhoserver.model.work.report.branch.repository.WorkReportBranchRepository;
import baekgwa.suhoserver.model.work.report.branch.repository.WorkReportBranchSerialRepository;
import baekgwa.suhoserver.model.work.report.report.entity.WorkReportEntity;
import baekgwa.suhoserver.model.work.report.straight.entity.WorkReportStraightEntity;
import baekgwa.suhoserver.model.work.report.straight.entity.WorkReportStraightSerialEntity;
import baekgwa.suhoserver.model.work.report.straight.repository.WorkReportStraightRepository;
import baekgwa.suhoserver.model.work.report.straight.repository.WorkReportStraightSerialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectWriteService {

	private final ProjectRepository projectRepository;
	private final ProjectBranchRepository projectBranchRepository;
	private final ProjectStraightRepository projectStraightRepository;
	private final ProjectStraightSerialRepository projectStraightSerialRepository;
	private final WorkReportStraightRepository workReportStraightRepository;
	private final WorkReportStraightSerialRepository workReportStraightSerialRepository;
	private final WorkReportBranchRepository workReportBranchRepository;
	private final WorkReportBranchSerialRepository workReportBranchSerialRepository;

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
	public List<ProjectBranchEntity> registerProjectBranchOrThrow(
		List<ProjectRequest.PostProjectBranchInfo> postProjectBranchInfoList,
		ProjectEntity findProject,
		Map<Long, BranchTypeEntity> findBranchTypeMap
	) {
		List<String> existBranchCode = projectBranchRepository.findAllByBranchTypeIdIn(
				findBranchTypeMap.keySet().stream().toList())
			.stream().map(data -> data.getBranchType().getCode()).toList();

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

		return projectBranchRepository.saveAll(newProjectBranchList);
	}

	/**
	 * 프로젝트에 신규 직선레일 등록
	 * @param postProjectStraightInfoList 등록할 직선레일 정보
	 * @param findProject 프로젝트 정보
	 * @param findStraightTypeMap 직선레일 타입 정보 Map<PK, Entity>
	 * @param straightInfoMap 직선레일 정보 [가공 위치, LitzWire 6개] 를 담은 Map
	 */
	@Transactional
	public List<ProjectStraightEntity> registerProjectStraightOrThrow(
		List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList,
		ProjectEntity findProject,
		Map<Long, StraightTypeEntity> findStraightTypeMap,
		Map<ProjectRequest.PostProjectStraightInfo, Map<String, Object>> straightInfoMap
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

					Map<String, Object> info = straightInfoMap.get(dto);
					BigDecimal holePosition = (BigDecimal) info.get("holePosition");
					BigDecimal[] wires = (BigDecimal[]) info.get("wires");

					return ProjectStraightEntity.createNewStraight(
						findProject,
						straightType,
						dto.getTotalQuantity(),
						dto.getIsLoopRail(),
						dto.getLength(),
						holePosition,
						wires);
				})
			.toList();

		// 3. 직선레일 전체 저장
		return projectStraightRepository.saveAll(newProjectStraightList);
	}

	/**
	 * 프로젝트에 할당된 특정 직선레일 할당 해제
	 * @param projectStraight 삭제할 프로젝트 직선레일 Entity
	 */
	@Transactional
	public void deleteProjectStraightOrThrow(ProjectStraightEntity projectStraight) {
		projectStraightRepository.delete(projectStraight);
	}

	/**
	 * 프로젝트에 할당된 직선레일 수정
	 * 추가적으로 Straight 의 Serial 관련 정보도 업데이트(soft delete or create) 처리
	 * @param projectStraight 프로젝트에 할당된 직선레일 Entity
	 * @param newQuantity 신규 수량
	 */
	@Transactional
	public void patchProjectStraightOrThrow(
		ProjectStraightEntity projectStraight, Long newQuantity
	) {
		projectStraight.patchProjectStraight(newQuantity);
	}

	/**
	 * 프로젝트에 할당된 분기레일 삭제
	 * @param projectBranch 프로젝트에 할당된 분기레일 Entity
	 */
	@Transactional
	public void deleteProjectBranch(ProjectBranchEntity projectBranch) {
		projectBranch.softDelete();
	}

	/**
	 * 프로젝트에 할당된 분기레일 수정
	 * @param projectBranch 프로젝트에 할당된 분기레일 Entity
	 * @param updateQuantity 업데이트 할 수량
	 */
	@Transactional
	public void patchProjectBranchOrThrow(ProjectBranchEntity projectBranch, Long updateQuantity) {
		projectBranch.patchProjectBranch(updateQuantity);
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

	/**
	 * 업무 보고된 수량만큼 프로젝트에 할당된 직선레일의 완료 수량에 업데이트 처리
	 * @param findWorkReport
	 * @return List<Long> 보고된 직선레일 PK List
	 */
	@Transactional
	public List<Long> applyStraightProductionQuantityFromReport(
		WorkReportEntity findWorkReport
	) {
		List<WorkReportStraightEntity> workReportStraightList =
			workReportStraightRepository.findByWorkReport(findWorkReport);

		if (workReportStraightList.isEmpty()) {
			return List.of();
		}

		Set<Long> projectStraightIdSet =
			workReportStraightList.stream()
				.map(WorkReportStraightEntity::getProjectStraightId)
				.collect(Collectors.toSet());

		Map<Long, ProjectStraightEntity> projectStraightMap =
			projectStraightRepository.findAllById(projectStraightIdSet)
				.stream().collect(Collectors.toMap(
					ProjectStraightEntity::getId,
					Function.identity()
				));

		workReportStraightList.forEach(wrs -> {
			ProjectStraightEntity ps =
				projectStraightMap.get(wrs.getProjectStraightId());

			if (ps == null) {
				throw new GlobalException(ErrorCode.REPORT_PROJECT_STRAIGHT_NOT_FOUND);
			}

			ps.updateCompleteQuantity(wrs.getProductionQuantity());
		});

		return workReportStraightList.stream().map(WorkReportStraightEntity::getId).toList();
	}

	@Transactional
	public List<Long> applyBranchProductionQuantityFromReport(
		WorkReportEntity findWorkReport
	) {
		List<WorkReportBranchEntity> workReportBranchList =
			workReportBranchRepository.findByWorkReport(findWorkReport);

		if (workReportBranchList.isEmpty()) {
			return List.of();
		}

		workReportBranchList.forEach(wrb -> {
			ProjectBranchEntity pb = wrb.getProjectBranch();
			pb.updateCompleteQuantity(wrb.getProductionQuantity());
		});

		return workReportBranchList.stream().map(WorkReportBranchEntity::getId).toList();
	}

	/**
	 * 보고서에 저장된 직선레일의 Serial List 로, 실제 제품의 생산 상태를 생산 완료로 업데이트 합니다.
	 * @param reportedStraightIdList 영향 받을 WorkReportStraight PK List
	 */
	public void markStraightSerialProduced(List<Long> reportedStraightIdList) {
		if (reportedStraightIdList.isEmpty()) {
			return;
		}

		List<WorkReportStraightSerialEntity> targetStraightSerialList =
			workReportStraightSerialRepository.findAllByWorkReportStraightIn(reportedStraightIdList);

		List<Long> targetProjectStraightserialIdList = targetStraightSerialList.stream()
			.map(WorkReportStraightSerialEntity::getProjectStraightSerialId)
			.toList();

		List<ProjectStraightSerialEntity> serialList = projectStraightSerialRepository.findReportTargetSerialList(
			targetProjectStraightserialIdList,
			ProductSerialState.ACTIVE,
			ProductProductionState.NOT_PRODUCED
		);

		serialList.forEach(ProjectStraightSerialEntity::markProduced);
	}

	/**
	 * 보고서에 저장된 분기 레일의 Serial List 로, 실제 제품의 생산 상태를 생산 완료로 업데이트 합니다.
	 * @param reportedBranchIdList
	 */
	@Transactional
	public void markBranchSerialProduced(List<Long> reportedBranchIdList) {
		if (reportedBranchIdList.isEmpty()) {
			return;
		}

		List<WorkReportBranchSerialEntity> targetBranchSerialList =
			workReportBranchSerialRepository.findAllByWorkReportBranchIn(reportedBranchIdList);

		targetBranchSerialList.forEach(wrb -> {
			ProjectBranchSerialEntity serial = wrb.getProjectBranchSerial();
			serial.markProduced();
		});
	}
}
