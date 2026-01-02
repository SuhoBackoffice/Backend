package baekgwa.suhoserver.model.work.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.work.report
 * FileName    : WorkReportStatus
 * Author      : Baekgwa
 * Date        : 25. 12. 29.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 29.     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum WorkReportStatus {

	PENDING("승인 대기"),
	APPROVED("승인"),
	REJECTED("반려");

	private final String description;
}
