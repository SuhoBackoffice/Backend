package baekgwa.suhoserver.domain.worker.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
