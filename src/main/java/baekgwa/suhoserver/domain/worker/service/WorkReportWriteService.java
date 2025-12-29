package baekgwa.suhoserver.domain.worker.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.worker.dto.WorkReportRequest;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.project.ProductProductionState;
import baekgwa.suhoserver.model.project.ProductSerialState;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.serial.entity.ProjectStraightSerialEntity;
import baekgwa.suhoserver.model.project.straight.serial.repository.ProjectStraightSerialRepository;
import baekgwa.suhoserver.model.project.straight.straight.repository.ProjectStraightRepository;
import baekgwa.suhoserver.model.user.entity.UserEntity;
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
		boolean existReport = workReportRepository.existsDailyReport(user.getId(), project.getId(), request.getWorkDate());
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
		WorkReportRequest.PostNewWorkReport request
	) {
		for (WorkReportRequest.PostNewWorkStraightReport straightReport : request.getStraightReportList()) {

			validateProjectStraight(savedWorkReport, straightReport);

			WorkReportStraightEntity workReportStraight =
				workReportStraightRepository.save(
					WorkReportStraightEntity.of(
						savedWorkReport,
						straightReport.getProjectStraightId(),
						straightReport.getProductionQuantity()
					)
				);

			Set<Long> unique = new HashSet<>(straightReport.getProjectStraightSerialIdList());
			if (unique.size() != straightReport.getProjectStraightSerialIdList().size()) {
				throw new GlobalException(ErrorCode.DUPLICATION_PRODUCTION_STRAIGHT_SERIAL);
			}

			if (straightReport.getProductionQuantity() != straightReport.getProjectStraightSerialIdList().size()) {
				throw new GlobalException(ErrorCode.NOT_MATCH_STRAIGHT_PRODUCTION_SERIAL_COUNT);
			}

			validateStraightSerial(
				straightReport.getProjectStraightId(),
				straightReport.getProjectStraightSerialIdList()
			);

			List<WorkReportStraightSerialEntity> straightSerialList = straightReport.getProjectStraightSerialIdList().stream()
				.map(l -> WorkReportStraightSerialEntity.of(workReportStraight, l))
				.toList();

			workReportStraightSerialRepository.saveAll(straightSerialList);
		}
	}

	private void validateProjectStraight(
		WorkReportEntity workReport,
		WorkReportRequest.PostNewWorkStraightReport request
	) {
		boolean exists = projectStraightRepository.existsByIdAndProjectId(
				request.getProjectStraightId(),
				workReport.getProject().getId()
			);

		if (!exists) {
			throw new GlobalException(ErrorCode.NOT_REGISTERED_PROJECT_STRAIGHT);
		}
	}

	private void validateStraightSerial(
		Long projectStraightId,
		List<Long> serialIds
	) {
		List<ProjectStraightSerialEntity> serialEntities =
			projectStraightSerialRepository.findAllByIdIn(serialIds);

		if (serialEntities.size() != serialIds.size()) {
			throw new GlobalException(ErrorCode.INVALID_STRAIGHT_SERIAL);
		}

		for (ProjectStraightSerialEntity serial : serialEntities) {
			if (!serial.getProjectStraight().getId().equals(projectStraightId)) {
				throw new GlobalException(ErrorCode.INVALID_STRAIGHT_SERIAL);
			}

			if (serial.getState() != ProductSerialState.ACTIVE) {
				throw new GlobalException(ErrorCode.INACTIVE_STRAIGHT_SERIAL);
			}

			if (serial.getProductionState() != ProductProductionState.NOT_PRODUCED) {
				throw new GlobalException(ErrorCode.ALREADY_PRODUCED_SERIAL);
			}

			boolean alreadyUsed =
				workReportStraightSerialRepository
					.existsByProjectStraightSerialId(serial.getId());

			if (alreadyUsed) {
				throw new GlobalException(ErrorCode.ALREADY_USED_STRAIGHT_SERIAL);
			}
		}
	}
}
