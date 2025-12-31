package baekgwa.suhoserver.domain.worker.dto;

import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
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
}
