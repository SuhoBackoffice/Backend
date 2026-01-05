package baekgwa.suhoserver.domain.worker.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.work.report.WorkReportStatus;
import baekgwa.suhoserver.model.work.report.branch.entity.WorkReportBranchEntity;
import baekgwa.suhoserver.model.work.report.branch.entity.WorkReportBranchSerialEntity;
import baekgwa.suhoserver.model.work.report.branch.repository.WorkReportBranchRepository;
import baekgwa.suhoserver.model.work.report.branch.repository.WorkReportBranchSerialRepository;
import baekgwa.suhoserver.model.work.report.report.entity.WorkReportEntity;
import baekgwa.suhoserver.model.work.report.report.repository.WorkReportRepository;
import baekgwa.suhoserver.model.work.report.straight.entity.WorkReportStraightEntity;
import baekgwa.suhoserver.model.work.report.straight.entity.WorkReportStraightSerialEntity;
import baekgwa.suhoserver.model.work.report.straight.repository.WorkReportStraightRepository;
import baekgwa.suhoserver.model.work.report.straight.repository.WorkReportStraightSerialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.domain.worker.service
 * FileName    : WorkReportReadService
 * Author      : Baekgwa
 * Date        : 25. 12. 31.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 31.     Baekgwa               Initial creation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkReportReadService {

	private final WorkReportRepository workReportRepository;
	private final WorkReportStraightRepository workReportStraightRepository;
	private final WorkReportStraightSerialRepository workReportStraightSerialRepository;
	private final WorkReportBranchRepository workReportBranchRepository;
	private final WorkReportBranchSerialRepository workReportBranchSerialRepository;

	/**
	 * 프로젝트에 이미 보고된 직선레일 중, pending 상태로 completed 수량에 포함되지 않은 수량 조회
	 * @param findProject 프로젝트 Entity
	 * @return Map key:ProjectStraightId, value:승인 대기중 수량
	 */
	@Transactional(readOnly = true)
	public Map<Long, Long> getPendingQuantityByProjectStraight(
		ProjectEntity findProject
	) {
		List<WorkReportEntity> pendingReportList =
			workReportRepository.findByProjectAndStatus(findProject, WorkReportStatus.PENDING);

		if (pendingReportList.isEmpty()) {
			log.debug("미승인 된, 업무 보고가 없습니다.");
			return Map.of();
		}

		List<WorkReportStraightEntity> findWorkStraightList
			= workReportStraightRepository.findByWorkReportIn(pendingReportList);

		return findWorkStraightList.stream()
			.collect(Collectors.groupingBy(
				WorkReportStraightEntity::getProjectStraightId,
				Collectors.summingLong(WorkReportStraightEntity::getProductionQuantity)
			));
	}

	/**
	 * 프로젝트에 이미 보고된 분기 레일 중, pending 상태로 completed 수량에 포함되지 않은 수량 조회
	 * @param findProject 프로젝트 Entity
	 * @return Map key:ProjectBranchId, value:승인 대기중 수량
	 */
	@Transactional(readOnly = true)
	public Map<Long, Long> getPendingQuantityByProjectBranch(
		ProjectEntity findProject
	) {
		List<WorkReportEntity> pendingReportList =
			workReportRepository.findByProjectAndStatus(findProject, WorkReportStatus.PENDING);

		if (pendingReportList.isEmpty()) {
			log.debug("미승인 된, 업무 보고가 없습니다.");
			return Map.of();
		}

		List<WorkReportBranchEntity> findWorkBranchList =
			workReportBranchRepository.findByWorkReportIn(pendingReportList);

		return findWorkBranchList.stream()
			.collect(Collectors.groupingBy(
				WorkReportBranchEntity::getProjectBranchId,
				Collectors.summingLong(WorkReportBranchEntity::getProductionQuantity)
			));
	}

	/**
	 * 프로젝트에 이미 보고된 특정 직선레일의 Serial 들 중 Pending 상태로 completed 수량에 포함되지 않은 직선레일 시리얼 PK List 조회
	 * @param projectStraightId 프로젝트 직선레일 PK
	 * @return Pending state ProjectStraightSerial PK List
	 */
	@Transactional(readOnly = true)
	public List<Long> getPendingProjectStraightSerialList(Long projectStraightId) {
		return workReportStraightRepository.findPendingSerialIdList(
			WorkReportStatus.PENDING,
			projectStraightId
		);
	}

	/**
	 * 보고서 정보를 조회합니다.
	 * @param reportId 보고서 PK
	 * @return new WorkReportEntity
	 */
	@Transactional(readOnly = true)
	public WorkReportEntity getWorkReportOrThrow(Long reportId) {
		return workReportRepository.findWithProjectById(reportId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_WORK_REPORT));
	}

	/**
	 * 보고서에 할당된 직선레일 목록을 조회.
	 * @param findWorkReport 보고서 Entity
	 * @return 보고된 직선레일 리스트
	 */
	@Transactional(readOnly = true)
	public List<WorkReportStraightEntity> getWorkReportStraight(WorkReportEntity findWorkReport) {
		return workReportStraightRepository.findByWorkReport(findWorkReport);
	}

	/**
	 * 보고서에 할당된 분기 레일 목록을 조회.
	 * @param findWorkReport 보고서 Entity
	 * @return 보고된 분기 레일 리스트
	 */
	@Transactional(readOnly = true)
	public List<WorkReportBranchEntity> getWorkReportBranch(
		WorkReportEntity findWorkReport
	) {
		return workReportBranchRepository.findByWorkReport(findWorkReport);
	}

	/**
	 * 업무보고에 해당하는 직선레일의 시리얼을 조회
	 * @param straightReports 업무보고 직선레일 목록
	 * @return key: WorkReportStraight PK value: StraightSerialList
	 */
	@Transactional(readOnly = true)
	public Map<Long, List<WorkReportStraightSerialEntity>> getWorkReportStraightSerialMap(
		List<WorkReportStraightEntity> straightReports
	) {
		List<Long> striaghtIdList = straightReports.stream()
			.map(WorkReportStraightEntity::getId)
			.toList();

		List<WorkReportStraightSerialEntity> serials =
			workReportStraightSerialRepository.findAllByStraightIds(striaghtIdList);

		return serials.stream()
			.collect(Collectors.groupingBy(
				s -> s.getWorkReportStraight().getId()
			));
	}

	/**
	 * 업무보고에 해당하는 분기 레일의 시리얼을 조회
	 * @param branchReports 업무보고 분기 레일 목록
	 * @return key: WorkReportBranch PK value: BranchSerialList
	 */
	public Map<Long, List<WorkReportBranchSerialEntity>> getWorkReportBranchSerialMap(
		List<WorkReportBranchEntity> branchReports
	) {
		List<Long> branchIdList = branchReports.stream()
			.map(WorkReportBranchEntity::getId)
			.toList();

		List<WorkReportBranchSerialEntity> serials =
			workReportBranchSerialRepository.findAllByBranchIds(branchIdList);

		return serials.stream()
			.collect(Collectors.groupingBy(
				b -> b.getWorkReportBranch().getId()
			));
	}

	@Transactional(readOnly = true)
	public List<WorkReportEntity> getWorkReportListByProject(ProjectEntity project, WorkReportStatus status) {

		List<WorkReportEntity> result;

		if (status == null) {
			result = workReportRepository.findByProjectOrderByWorkDateDesc(project);
		} else {
			result = workReportRepository.findByProjectAndStatusOrderByWorkDateDesc(project, status);
		}

		return result;
	}
}
