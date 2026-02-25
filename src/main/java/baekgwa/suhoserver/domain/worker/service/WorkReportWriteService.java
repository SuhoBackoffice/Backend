package baekgwa.suhoserver.domain.worker.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.worker.dto.WorkReportRequest;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.serial.entity.ProjectBranchSerialEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.serial.entity.ProjectStraightSerialEntity;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.user.entity.UserEntity;
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

/**
 * PackageName : baekgwa.suhoserver.domain.worker.service
 * FileName    : WorkReportWriteService
 * Author      : Baekgwa
 * Date        : 25. 12. 29.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 29.     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class WorkReportWriteService {

	private final WorkReportRepository workReportRepository;
	private final WorkReportStraightRepository workReportStraightRepository;
	private final WorkReportStraightSerialRepository workReportStraightSerialRepository;
	private final WorkReportBranchRepository workReportBranchRepository;
	private final WorkReportBranchSerialRepository workReportBranchSerialRepository;

	/**
	 * 새로운 업무 보고서를 작성합니다.
	 * 하루에 한번의 업무 보고서만 작성할 수 있습니다.
	 * @param user 업무 보고하는 회원 정보
	 * @param project 저장할 Project
	 * @param request 보고서 내용
	 * @return saved WorkReport Entity
	 */
	@Transactional
	public WorkReportEntity createNewWorkReport(
		UserEntity user,
		ProjectEntity project,
		WorkReportRequest.PostNewWorkReport request
	) {
		List<WorkReportStatus> statusList = List.of(WorkReportStatus.APPROVED, WorkReportStatus.PENDING);
		boolean existReport = workReportRepository.existsDailyReport(
			user.getId(),
			project.getId(),
			request.getWorkDate(),
			statusList
		);
		if (existReport) {
			throw new GlobalException(ErrorCode.ALREADY_EXIST_DAILY_REPORT);
		}

		WorkReportEntity newWorkReport =
			WorkReportEntity.of(user, project, request.getWorkSummary(), request.getWorkDate());

		return workReportRepository.save(newWorkReport);
	}

	@Transactional
	public void createNewStraightWorkReport(
		WorkReportEntity savedWorkReport,
		WorkReportRequest.PostNewWorkReport request,
		Map<Long, List<ProjectStraightSerialEntity>> straightSerialMap,
		Map<Long, ProjectStraightEntity> projectStraightMap
	) {
		if (request.getStraightReportList().isEmpty()) {
			return;
		}

		for (WorkReportRequest.PostNewWorkStraightReport straightReport : request.getStraightReportList()) {
			ProjectStraightEntity ps = projectStraightMap.get(straightReport.getProjectStraightId());

			WorkReportStraightEntity workReportStraight =
				workReportStraightRepository.save(
					WorkReportStraightEntity.of(
						savedWorkReport,
						ps,
						straightReport.getProductionQuantity()
					)
				);

			List<ProjectStraightSerialEntity> serialList =
				straightSerialMap.get(straightReport.getProjectStraightId());

			List<WorkReportStraightSerialEntity> straightSerialList =
				serialList.stream()
					.map(serial ->
						WorkReportStraightSerialEntity.of(
							workReportStraight,
							serial
						)
					)
					.toList();

			workReportStraightSerialRepository.saveAll(straightSerialList);
		}
	}

	@Transactional
	public void createNewBranchWorkReport(
		WorkReportEntity savedWorkReport,
		WorkReportRequest.PostNewWorkReport request,
		Map<Long, ProjectBranchEntity> projectBranchMap,
		Map<Long, List<ProjectBranchSerialEntity>> branchSerialMap
	) {
		for (WorkReportRequest.PostNewWorkBranchReport branch : request.getBranchReportList()) {
			ProjectBranchEntity pb = projectBranchMap.get(branch.getProjectBranchId());

			WorkReportBranchEntity workReportBranch =
				workReportBranchRepository.save(
					WorkReportBranchEntity.of(
						savedWorkReport,
						pb,
						branch.getProductionQuantity()
					)
				);

			List<ProjectBranchSerialEntity> serialList =
				branchSerialMap.get(branch.getProjectBranchId());

			List<WorkReportBranchSerialEntity> branchSerialList =
				serialList.stream()
					.map(serial ->
						WorkReportBranchSerialEntity.of(
							workReportBranch,
							serial
						)
					)
					.toList();

			workReportBranchSerialRepository.saveAll(branchSerialList);
		}
	}

	@Transactional
	public void updateWorkReport(
		WorkReportEntity findWorkReport,
		WorkReportRequest.PostDailyReport request
	) {
		if (request.isApproved() && request.getRejectReason() != null) {
			throw new GlobalException(ErrorCode.REPORT_APPROVED_BUT_REJECT_REASON_EXIST);
		}

		if (request.isRejected() && request.getRejectReason() == null) {
			throw new GlobalException(ErrorCode.REPORT_REJECTED_BUT_REJECT_REASON_NOT_EXIST);
		}

		if (!findWorkReport.isEditable()) {
			throw new GlobalException(ErrorCode.ALREADY_UPDATED_REPORT);
		}

		findWorkReport.updateStatus(request.getStatus(), request.getRejectReason());
	}
}
