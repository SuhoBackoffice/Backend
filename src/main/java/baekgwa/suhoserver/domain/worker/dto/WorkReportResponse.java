package baekgwa.suhoserver.domain.worker.dto;

import java.time.LocalDate;
import java.util.List;

import baekgwa.suhoserver.model.project.straight.serial.entity.ProjectStraightSerialEntity;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.user.entity.UserEntity;
import baekgwa.suhoserver.model.work.report.WorkReportStatus;
import baekgwa.suhoserver.model.work.report.report.entity.WorkReportEntity;
import baekgwa.suhoserver.model.work.report.straight.entity.WorkReportStraightEntity;
import baekgwa.suhoserver.model.work.report.straight.entity.WorkReportStraightSerialEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.domain.worker.dto
 * FileName    : WorkReportResponse
 * Author      : Baekgwa
 * Date        : 25. 12. 29.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 29.     Baekgwa               Initial creation
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkReportResponse {

	@Getter
	@Builder(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class GetWorkReportDetail {
		private final boolean isOwner;
		private final String reportUserName;
		private final String workSummary;
		private final LocalDate workDate;
		private final WorkReportStatus status;
		private final Long projectId;
		private final String region;
		private final String projectName;

		private final List<WorkReportStraight> straightReports;
		// todo: 분기레일 정보 추가
		// private final List<WorkReportBranch> branchReports;

		public static GetWorkReportDetail of(
			WorkReportEntity workReport,
			UserEntity loginUser,
			List<WorkReportStraight> straightReports
		) {
			return GetWorkReportDetail
				.builder()
				.isOwner(loginUser.getId().equals(workReport.getReportUserId()))
				.reportUserName(workReport.getReportUserName())
				.workSummary(workReport.getWorkSummary())
				.workDate(workReport.getWorkDate())
				.status(workReport.getStatus())
				.projectId(workReport.getProject().getId())
				.region(workReport.getProject().getRegion())
				.projectName(workReport.getProject().getName())
				.straightReports(straightReports)
				.build();
		}
	}

	@Getter
	@Builder(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class WorkReportStraight {
		private final String serial;
		private final Long projectStraightId;
		private final Long productionQuantity;
		private final List<WorkReportStraightSerial> productionSerials;

		public static WorkReportStraight of(
			WorkReportStraightEntity reportedStraight,
			List<WorkReportStraightSerial> serialList
		) {
			return WorkReportStraight
				.builder()
				.serial(reportedStraight.getSerial())
				.projectStraightId(reportedStraight.getProjectStraightId())
				.productionQuantity(reportedStraight.getProductionQuantity())
				.productionSerials(serialList)
				.build();
		}
	}

	@Getter
	@Builder(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class WorkReportStraightSerial {
		private final Long projectStraightSerialId;
		private final String serial;

		public static WorkReportStraightSerial from(WorkReportStraightSerialEntity serial) {
			return WorkReportStraightSerial
				.builder()
				.projectStraightSerialId(serial.getProjectStraightSerialId())
				.serial(serial.getSerial())
				.build();
		}
	}

	@Getter
	public static class PostNewWorkReport {
		private final Long workReportId;

		@Builder
		private PostNewWorkReport(Long workReportId) {
			this.workReportId = workReportId;
		}
	}

	@Getter
	@Builder(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class GetProjectStraight {
		private Long projectStraightId; // 프로젝트 직선레일 PK
		private String straightSerial; // 직선레일 식별 번호
		private Long totalQuantity; // 총 수량
		private Long completedQuantity; // 완료 수량
		private Long pendingQuantity; // 보고 후, 승인 대기중인 수량
		private Long availableQuantity; // 보고 가능한 수량

		public static GetProjectStraight of(
			ProjectStraightEntity straight,
			long pendingQuantity,
			String straightSerial
		) {
			long total = straight.getTotalQuantity();
			long completed = straight.getCompletedQuantity();
			long available = total - completed - pendingQuantity;

			if (available <= 0) {
				log.debug("{}는 전체 {}개 중 {}개 생산 완료, {}개 승인 대기중으로 더이상 보고할 수 없습니다.",
					straightSerial,
					straight.getTotalQuantity(),
					straight.getCompletedQuantity(),
					pendingQuantity);
				return null;
			}

			return GetProjectStraight.builder()
				.projectStraightId(straight.getId())
				.straightSerial(straightSerial)
				.totalQuantity(total)
				.completedQuantity(completed)
				.pendingQuantity(pendingQuantity)
				.availableQuantity(available)
				.build();
		}
	}

	@Getter
	@Builder(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class GetProjectStraightSerial {
		private Long projectStraightId;
		private String serial;

		public static GetProjectStraightSerial from(
			ProjectStraightSerialEntity projectStraightSerial
		) {
			return GetProjectStraightSerial
				.builder()
				.projectStraightId(projectStraightSerial.getId())
				.serial(projectStraightSerial.getSerial())
				.build();
		}
	}
}
