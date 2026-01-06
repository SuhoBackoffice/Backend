package baekgwa.suhoserver.domain.worker.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.worker.dto.WorkReportRequest;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.factory.ProductSerialFactory;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.branch.repository.ProjectBranchRepository;
import baekgwa.suhoserver.model.project.branch.serial.repository.ProjectBranchSerialRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.serial.repository.ProjectStraightSerialRepository;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.project.straight.straight.repository.ProjectStraightRepository;
import baekgwa.suhoserver.model.user.entity.UserEntity;
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
	private final ProjectStraightRepository projectStraightRepository;
	private final ProjectStraightSerialRepository projectStraightSerialRepository;
	private final ProjectBranchRepository projectBranchRepository;
	private final WorkReportBranchRepository workReportBranchRepository;
	private final ProjectBranchSerialRepository projectBranchSerialRepository;
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
		boolean existReport = workReportRepository.existsDailyReport(user.getId(), project.getId(),
			request.getWorkDate());
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
		Map<Long, Map<Long, String>> straightSerialSnapshot,
		Map<Long, ProjectStraightEntity> projectStraightMap
	) {
		if (request.getStraightReportList().isEmpty()) {
			return;
		}

		for (WorkReportRequest.PostNewWorkStraightReport straightReport : request.getStraightReportList()) {
			ProjectStraightEntity ps = projectStraightMap.get(straightReport.getProjectStraightId());
			String serial = ProductSerialFactory.generateStraightSerial(
				ps.getLength(),
				ps.getIsLoopRail(),
				ps.getStraightType().getType()
			);

			WorkReportStraightEntity workReportStraight =
				workReportStraightRepository.save(
					WorkReportStraightEntity.of(
						savedWorkReport,
						straightReport.getProjectStraightId(),
						straightReport.getProductionQuantity(),
						serial
					)
				);

			Map<Long, String> serialSnapshot =
				straightSerialSnapshot.get(straightReport.getProjectStraightId());

			List<WorkReportStraightSerialEntity> straightSerialList =
				straightReport.getProjectStraightSerialIdList().stream()
					.map(serialId ->
						WorkReportStraightSerialEntity.of(
							workReportStraight,
							serialId,
							serialSnapshot.get(serialId)
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
		Map<Long, Map<Long, String>> branchSerialSnapshot
	) {
		for (WorkReportRequest.PostNewWorkBranchReport branch : request.getBranchReportList()) {
			ProjectBranchEntity pb = projectBranchMap.get(branch.getProjectBranchId());
			String serial = ProductSerialFactory.generateBranchSerial(pb.getBranchType().getCode());

			WorkReportBranchEntity workReportBranch =
				workReportBranchRepository.save(
					WorkReportBranchEntity.of(
						savedWorkReport,
						branch.getProjectBranchId(),
						branch.getProductionQuantity(),
						serial
					)
				);

			Map<Long, String> serialSnapshot =
				branchSerialSnapshot.get(branch.getProjectBranchId());

			List<WorkReportBranchSerialEntity> branchSerialList =
				branch.getProjectBranchSerialIdList().stream()
					.map(serialId ->
						WorkReportBranchSerialEntity.of(
							workReportBranch,
							serialId,
							serialSnapshot.get(serialId)
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
		if(request.isApproved() && request.getRejectReason() != null) {
			throw new GlobalException(ErrorCode.REPORT_APPROVED_BUT_REJECT_REASON_EXIST);
		}

		if(request.isRejected() && request.getRejectReason() == null) {
			throw new GlobalException(ErrorCode.REPORT_REJECTED_BUT_REJECT_REASON_NOT_EXIST);
		}

		// 이미 반려된 작업 보고서는 수정이 불가능 합니다.
		if(!findWorkReport.isEditable()) {
			throw new GlobalException(ErrorCode.ALREADY_UPDATED_REPORT);
		}

		findWorkReport.updateStatus(request.getStatus(), request.getRejectReason());
	}
}
