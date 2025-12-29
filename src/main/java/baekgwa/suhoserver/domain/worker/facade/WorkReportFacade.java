package baekgwa.suhoserver.domain.worker.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.project.service.ProjectReadService;
import baekgwa.suhoserver.domain.user.service.UserService;
import baekgwa.suhoserver.domain.worker.dto.WorkReportRequest;
import baekgwa.suhoserver.domain.worker.dto.WorkReportResponse;
import baekgwa.suhoserver.domain.worker.service.WorkReportWriteService;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.user.entity.UserEntity;
import baekgwa.suhoserver.model.work.report.report.entity.WorkReportEntity;
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

	private final WorkReportWriteService workReportWriteService;

	private final UserService userService;
	private final ProjectReadService projectReadService;

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

		workReportWriteService.createNewStraightWorkReport(
			savedWorkReport,
			request
		);

		// todo : 관리자에게 알림 발송

		return WorkReportResponse.PostNewWorkReport
			.builder()
			.workReportId(savedWorkReport.getId())
			.build();
	}
}
