package baekgwa.suhoserver.domain.worker.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.suhoserver.domain.worker.dto.WorkReportRequest;
import baekgwa.suhoserver.domain.worker.dto.WorkReportResponse;
import baekgwa.suhoserver.domain.worker.facade.WorkReportFacade;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import baekgwa.suhoserver.model.work.report.WorkReportStatus;
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

	@GetMapping("/{reportId}")
	@Operation(summary = "일일 업무 보고 상세 조회")
	public BaseResponse<WorkReportResponse.GetWorkReportDetail> getDailyReport(
		@PathVariable("reportId") Long reportId,
		@AuthenticationPrincipal Long userId
	) {
		WorkReportResponse.GetWorkReportDetail response =
			workReportFacade.getWorkReportDetail(reportId, userId);

		return BaseResponse.success(SuccessCode.GET_WORK_REPORT_DETAIL_SUCCESS, response);
	}

	@GetMapping("/project/{projectId}")
	@Operation(summary = "프로젝트의 업무 보고 목록 보기")
	public BaseResponse<List<WorkReportResponse.GetProjectWorkReport>> getProjectWorkReport(
		@PathVariable("projectId") Long projectId,
		@RequestParam(value = "status", required = false) WorkReportStatus status
	) {
		List<WorkReportResponse.GetProjectWorkReport> response =
			workReportFacade.getWorkReportList(projectId, status);

		return BaseResponse.success(SuccessCode.GET_PROJECT_WORK_REPORT_SUCCESS, response);
	}

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

	@GetMapping("/project/{projectId}/straight/{straightId}/serial")
	@Operation(summary = "프로젝트에서 보고 가능한 직선 레일의 시리얼 목록 반환")
	public BaseResponse<List<WorkReportResponse.GetProjectStraightSerial>> getAbleReportStraightSerialList(
		@PathVariable("projectId") Long projectId,
		@PathVariable("straightId") Long straightId
	) {
		List<WorkReportResponse.GetProjectStraightSerial> response =
			workReportFacade.getAbleReportStraightSerialList(straightId);

		return BaseResponse.success(SuccessCode.GET_ABLE_REPORT_STRAIGHT_SERIAL_LIST_SUCCESS, response);
	}

	@GetMapping("/project/{projectId}/branch/{branchId}/serial")
	@Operation(summary = "프로젝트에서 보고 가능한 분기 레일의 시리얼 목록 반환")
	public BaseResponse<List<WorkReportResponse.GetProjectBranchSerial>> getAbleReportBranchSerialList(
		@PathVariable("projectId") Long projectId,
		@PathVariable("branchId") Long branchId
	) {
		List<WorkReportResponse.GetProjectBranchSerial> response =
			workReportFacade.getAbleReportBranchSerialList(branchId);

		return BaseResponse.success(SuccessCode.GET_ABLE_REPORT_STRAIGHT_SERIAL_LIST_SUCCESS, response);
	}

	@GetMapping("/project/{projectId}/branch")
	@Operation(summary = "프로젝트에서 보고 가능한 분기 레일 목록 반환")
	public BaseResponse<List<WorkReportResponse.GetProjectBranch>> getAbleReportBranchList(
		@PathVariable("projectId") Long projectId
	) {
		List<WorkReportResponse.GetProjectBranch> response =
			workReportFacade.getAbleReportBranchList(projectId);

		return BaseResponse.success(SuccessCode.GET_ABLE_REPORT_BRANCH_LIST_SUCCESS, response);
	}
}
