package baekgwa.suhoserver.domain.worker.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.worker.dto
 * FileName    : WorkReportRequest
 * Author      : Baekgwa
 * Date        : 25. 12. 29.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 29.     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkReportRequest {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class PostNewWorkReport {
		private String workSummary;

		@PastOrPresent(message = "업무 보고는 오늘 까지만 선택 가능합니다.")
		private LocalDate workDate;
		private List<PostNewWorkStraightReport> straightReportList;
		//todo: 분기레일 추가
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class PostNewWorkStraightReport {

		@Min(value = 1L, message = "유효한 직선레일을 선택해 주세요.")
		private Long projectStraightId;

		@Min(value = 1L, message = "생산 수량은 하나 이상이여야 합니다.")
		private Long productionQuantity;

		@Size(min = 1, message = "생산한 직선레일 시리얼 번호는 1개 이상 선택해주세요.")
		@NotNull(message = "생산한 직선레일 시리얼 번호는 1개 이상 선택해주세요.")
		private List<Long> projectStraightSerialIdList;
	}
}
