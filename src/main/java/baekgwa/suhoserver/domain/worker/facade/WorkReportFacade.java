package baekgwa.suhoserver.domain.worker.facade;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.project.service.ProjectReadService;
import baekgwa.suhoserver.domain.user.service.UserService;
import baekgwa.suhoserver.domain.worker.dto.WorkReportRequest;
import baekgwa.suhoserver.domain.worker.dto.WorkReportResponse;
import baekgwa.suhoserver.domain.worker.service.WorkReportReadService;
import baekgwa.suhoserver.domain.worker.service.WorkReportWriteService;
import baekgwa.suhoserver.global.factory.ProductSerialFactory;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.serial.entity.ProjectStraightSerialEntity;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.user.entity.UserEntity;
import baekgwa.suhoserver.model.work.report.WorkReportStatus;
import baekgwa.suhoserver.model.work.report.branch.entity.WorkReportBranchEntity;
import baekgwa.suhoserver.model.work.report.branch.entity.WorkReportBranchSerialEntity;
import baekgwa.suhoserver.model.work.report.report.entity.WorkReportEntity;
import baekgwa.suhoserver.model.work.report.straight.entity.WorkReportStraightEntity;
import baekgwa.suhoserver.model.work.report.straight.entity.WorkReportStraightSerialEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.worker.facade
 * FileName    : WorkReportFacade
 * Author      : Baekgwa
 * Date        : 25. 12. 28.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 28.     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class WorkReportFacade {

	private final UserService userService;
	private final ProjectReadService projectReadService;
	private final WorkReportReadService workReportReadService;
	private final WorkReportWriteService workReportWriteService;

	@Transactional
	public WorkReportResponse.PostNewWorkReport createDailyReport(
		WorkReportRequest.PostNewWorkReport request,
		Long projectId,
		Long userId
	) {
		UserEntity findUser = userService.getUserEntityOrThrow(userId);
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);

		WorkReportEntity savedWorkReport = workReportWriteService.createNewWorkReport(
			findUser,
			findProject,
			request
		);

		Map<Long, ProjectStraightEntity> projectStraightMap =
			projectReadService.getProjectStraightMap(request);

		Map<Long, Map<Long, String>> straightSerialSnapshot =
			projectReadService.getStraightSerialSnapshot(request);

		workReportWriteService.createNewStraightWorkReport(
			savedWorkReport,
			request,
			straightSerialSnapshot,
			projectStraightMap
		);

		Map<Long, ProjectBranchEntity> projectBranchMap =
			projectReadService.getProjectBranchMap(request);

		Map<Long, Map<Long, String>> branchSerialSnapshot =
			projectReadService.getBranchSerialSnapshot(request);

		workReportWriteService.createNewBranchWorkReport(
			savedWorkReport,
			request,
			projectBranchMap,
			branchSerialSnapshot
		);

		// todo : 관리자에게 알림 발송

		return WorkReportResponse.PostNewWorkReport
			.builder()
			.workReportId(savedWorkReport.getId())
			.build();
	}

	@Transactional(readOnly = true)
	public List<WorkReportResponse.GetProjectStraight> getAbleReportStraightList(Long projectId) {
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);

		Map<Long, Long> pendingQuantityMap =
			workReportReadService.getPendingQuantityByProjectStraight(findProject);

		List<ProjectStraightEntity> unCompletedStraightList =
			projectReadService.getUnCompletedProjectStraightList(findProject);

		return unCompletedStraightList.stream()
			.map(straight -> WorkReportResponse.GetProjectStraight.of(
					straight,
					pendingQuantityMap.getOrDefault(straight.getId(), 0L),
					ProductSerialFactory.generateStraightSerial(straight.getLength(), straight.getIsLoopRail(), straight.getStraightType().getType())
				))
			.filter(Objects::nonNull)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<WorkReportResponse.GetProjectBranch> getAbleReportBranchList(Long projectId) {
		ProjectEntity findProject = projectReadService.getProjectOrThrow(projectId);

		Map<Long, Long> pendingQuantityMap =
			workReportReadService.getPendingQuantityByProjectBranch(findProject);

		List<ProjectBranchEntity> unCompletedBranchList =
			projectReadService.getUnCompletedProjectBranchList(findProject);

		return unCompletedBranchList.stream()
			.map(branch -> WorkReportResponse.GetProjectBranch.of(
				branch,
				pendingQuantityMap.getOrDefault(branch.getId(), 0L),
				ProductSerialFactory.generateBranchSerial(branch.getBranchType().getCode())
			))
			.filter(Objects::nonNull)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<WorkReportResponse.GetProjectStraightSerial> getAbleReportStraightSerialList(
		Long projectStraightId
	) {
		List<ProjectStraightSerialEntity> allSerialList =
			projectReadService.getProjectStraightSerialList(projectStraightId);

		List<Long> pendingSerialPKList =
			workReportReadService.getPendingProjectStraightSerialList(projectStraightId);

		return allSerialList.stream()
			.filter(serial -> !pendingSerialPKList.contains(serial.getId()))
			.map(WorkReportResponse.GetProjectStraightSerial::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public WorkReportResponse.GetWorkReportDetail getWorkReportDetail(Long reportId, Long userId) {
		UserEntity loginUser = userService.getUserEntityOrThrow(userId);

		WorkReportEntity findWorkReport = workReportReadService.getWorkReportOrThrow(reportId);

		List<WorkReportStraightEntity> straightReports =
			workReportReadService.getWorkReportStraight(findWorkReport);

		Map<Long, List<WorkReportStraightSerialEntity>> straightSerialMap =
			workReportReadService.getWorkReportStraightSerialMap(straightReports);

		List<WorkReportResponse.WorkReportStraight> workReportStraightList = straightReports.stream()
			.map(straight -> {
				List<WorkReportResponse.WorkReportStraightSerial> serialList =
					straightSerialMap.getOrDefault(straight.getId(), List.of())
						.stream()
						.map(WorkReportResponse.WorkReportStraightSerial::from)
						.toList();

				return WorkReportResponse.WorkReportStraight.of(straight, serialList);
			})
			.toList();

		List<WorkReportBranchEntity> branchReports =
			workReportReadService.getWorkReportBranch(findWorkReport);

		Map<Long, List<WorkReportBranchSerialEntity>> branchSerialMap =
			workReportReadService.getWorkReportBranchSerialMap(branchReports);

		List<WorkReportResponse.WorkReportBranch> workReportBranchList = branchReports.stream()
			.map(branch -> {
				List<WorkReportResponse.WorkReportBranchSerial> serialList = branchSerialMap.getOrDefault(
						branch.getId(),
						List.of())
					.stream()
					.map(WorkReportResponse.WorkReportBranchSerial::from)
					.toList();

				return WorkReportResponse.WorkReportBranch.of(branch, serialList);
			})
			.toList();

		return WorkReportResponse.GetWorkReportDetail.of(
			findWorkReport, loginUser, workReportStraightList, workReportBranchList
		);
	}

	@Transactional(readOnly = true)
	public List<WorkReportResponse.GetProjectWorkReport> getWorkReportList(Long projectId, WorkReportStatus status) {
		ProjectEntity project =
			projectReadService.getProjectOrThrow(projectId);

		List<WorkReportEntity> findWorkReportList =
			workReportReadService.getWorkReportListByProject(project, status);

		return findWorkReportList.stream()
			.map(WorkReportResponse.GetProjectWorkReport::from)
			.toList();
	}
}
