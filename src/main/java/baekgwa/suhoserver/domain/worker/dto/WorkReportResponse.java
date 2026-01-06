package baekgwa.suhoserver.domain.worker.dto;

import java.time.LocalDate;
import java.util.List;

import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.serial.entity.ProjectBranchSerialEntity;
import baekgwa.suhoserver.model.project.straight.serial.entity.ProjectStraightSerialEntity;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.user.entity.UserEntity;
import baekgwa.suhoserver.model.work.report.branch.entity.WorkReportBranchEntity;
import baekgwa.suhoserver.model.work.report.branch.entity.WorkReportBranchSerialEntity;
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
	public static class GetProjectWorkReport {
		private final Long workReportId;
		private final String reportUserName;
		private final String workSummary;
		private final LocalDate workDate;
		private final String status;

		public static GetProjectWorkReport from(WorkReportEntity workReport) {
			return GetProjectWorkReport
				.builder()
				.workReportId(workReport.getId())
				.reportUserName(workReport.getReportUserName())
				.workSummary(workReport.getWorkSummary())
				.workDate(workReport.getWorkDate())
				.status(workReport.getStatus().getDescription())
				.build();
		}
	}

	@Getter
	@Builder(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class GetWorkReportDetail {
		private final boolean isOwner;
		private final String reportUserName;
		private final String workSummary;
		private final LocalDate workDate;
		private final String status;
		private final String rejectReason;
		private final Long projectId;
		private final String region;
		private final String projectName;

		private final List<WorkReportStraight> straightReports;
		private final List<WorkReportBranch> branchReports;

		public static GetWorkReportDetail of(
			WorkReportEntity workReport,
			UserEntity loginUser,
			List<WorkReportStraight> straightReports,
			List<WorkReportBranch> branchReports
		) {
			return GetWorkReportDetail
				.builder()
				.isOwner(loginUser.getId().equals(workReport.getReportUserId()))
				.reportUserName(workReport.getReportUserName())
				.workSummary(workReport.getWorkSummary())
				.workDate(workReport.getWorkDate())
				.status(workReport.getStatus().name())
				.rejectReason(workReport.getRejectReason())
				.projectId(workReport.getProject().getId())
				.region(workReport.getProject().getRegion())
				.projectName(workReport.getProject().getName())
				.straightReports(straightReports)
				.branchReports(branchReports)
				.build();
		}
	}

	@Getter
	@Builder(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class WorkReportBranch {
		private final String branchSerial;
		private final Long projectBranchId;
		private final Long productionQuantity;
		private final List<WorkReportBranchSerial> productionSerials;

		public static WorkReportBranch of(
			WorkReportBranchEntity reportedBranch,
			List<WorkReportBranchSerial> serialList
		) {
			return WorkReportBranch
				.builder()
				.branchSerial(reportedBranch.getSerial())
				.projectBranchId(reportedBranch.getProjectBranchId())
				.productionQuantity(reportedBranch.getProductionQuantity())
				.productionSerials(serialList)
				.build();
		}
	}

	@Getter
	@Builder(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class WorkReportStraight {
		private final String straightSerial;
		private final Long projectStraightId;
		private final Long productionQuantity;
		private final List<WorkReportStraightSerial> productionSerials;

		public static WorkReportStraight of(
			WorkReportStraightEntity reportedStraight,
			List<WorkReportStraightSerial> serialList
		) {
			return WorkReportStraight
				.builder()
				.straightSerial(reportedStraight.getSerial())
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
	@Builder(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class WorkReportBranchSerial {
		private final Long projectBranchSerialId;
		private final String serial;

		public static WorkReportBranchSerial from(WorkReportBranchSerialEntity serial) {
			return WorkReportBranchSerial
				.builder()
				.projectBranchSerialId(serial.getProjectBranchSerialId())
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
	public static class GetProjectBranch {
		private final Long projectBranchId;
		private final String branchSerial;
		private final Long totalQuantity;
		private final Long completedQuantity;
		private final Long pendingQuantity;
		private final Long availableQuantity;

		public static GetProjectBranch of(
			ProjectBranchEntity branch,
			long pendingQuantity,
			String branchSerial
		) {
			long total = branch.getTotalQuantity();
			long completed = branch.getCompletedQuantity();
			long available = total - completed - pendingQuantity;

			if (available <= 0) {
				log.debug("[분기레일] {}는 전체 {}개 중 {}개 생산 완료, {}개 승인 대기중으로 더이상 보고할 수 없습니다.",
					branchSerial,
					branch.getTotalQuantity(),
					branch.getCompletedQuantity(),
					pendingQuantity);
				return null;
			}

			return GetProjectBranch.builder()
				.projectBranchId(branch.getId())
				.branchSerial(branchSerial)
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
	public static class GetProjectStraight {
		private final Long projectStraightId; // 프로젝트 직선레일 PK
		private final String straightSerial; // 직선레일 식별 번호
		private final Long totalQuantity; // 총 수량
		private final Long completedQuantity; // 완료 수량
		private final Long pendingQuantity; // 보고 후, 승인 대기중인 수량
		private final Long availableQuantity; // 보고 가능한 수량

		public static GetProjectStraight of(
			ProjectStraightEntity straight,
			long pendingQuantity,
			String straightSerial
		) {
			long total = straight.getTotalQuantity();
			long completed = straight.getCompletedQuantity();
			long available = total - completed - pendingQuantity;

			if (available <= 0) {
				log.debug("[직선레일] {}는 전체 {}개 중 {}개 생산 완료, {}개 승인 대기중으로 더이상 보고할 수 없습니다.",
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
	public static class GetProjectBranchSerial {
		private final Long branchSerialId;
		private final String serial;

		public static GetProjectBranchSerial from(
			ProjectBranchSerialEntity projectBranchSerial
		) {
			return GetProjectBranchSerial
				.builder()
				.branchSerialId(projectBranchSerial.getId())
				.serial(projectBranchSerial.getSerial())
				.build();
		}
	}

	@Getter
	@Builder(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class GetProjectStraightSerial {
		private final Long straightSerialId;
		private final String serial;

		public static GetProjectStraightSerial from(
			ProjectStraightSerialEntity projectStraightSerial
		) {
			return GetProjectStraightSerial
				.builder()
				.straightSerialId(projectStraightSerial.getId())
				.serial(projectStraightSerial.getSerial())
				.build();
		}
	}
}
