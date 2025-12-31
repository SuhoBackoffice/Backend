package baekgwa.suhoserver.domain.worker.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.suhoserver.domain.worker.dto.WorkReportRequest;
import baekgwa.suhoserver.domain.worker.dto.WorkReportResponse;
import baekgwa.suhoserver.domain.worker.facade.WorkReportFacade;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.worker.controller
 * FileName    : WorkReportController
 * Author      : Baekgwa
 * Date        : 25. 12. 28.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 28.     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/work/report")
@Tag(name = "Work Report Controller", description = "작업자의 업무 보고 처리용 controller")
public class WorkReportController {

	private final WorkReportFacade workReportFacade;

	@PostMapping("/project/{projectId}/")
	@Operation(summary = "일일 프로젝트 업무 보고")
	public BaseResponse<WorkReportResponse.PostNewWorkReport> newDailyReport(
		@Valid @RequestBody WorkReportRequest.PostNewWorkReport request,
		@PathVariable("projectId") Long projectId,
		@AuthenticationPrincipal Long userId
	){
		WorkReportResponse.PostNewWorkReport response =
			workReportFacade.createDailyReport(request, projectId, userId);

		return BaseResponse.success(SuccessCode.POST_WORK_REPORT_SUCCESS, response);
	}

	@GetMapping("/project/{projectId}/straight")
	@Operation(summary = "프로젝트에서 보고 가능한 직선 레일 목록 반환")
	public BaseResponse<List<WorkReportResponse.GetProjectStraight>> getAbleReportStraightList(
		@PathVariable("projectId") Long projectId
	) {
		List<WorkReportResponse.GetProjectStraight> response =
			workReportFacade.getAbleReportStraightList(projectId);

		return BaseResponse.success(SuccessCode.GET_ABLE_REPORT_STRAIGHT_LIST_SUCCESS, response);
	}
}
