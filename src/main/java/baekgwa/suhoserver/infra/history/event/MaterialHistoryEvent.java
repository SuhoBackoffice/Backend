package baekgwa.suhoserver.infra.history.event;

import java.util.List;

/**
 * PackageName : baekgwa.suhoserver.infra.history.event
 * FileName    : MaterialHistoryEvent
 * Author      : Baekgwa
 * Date        : 2025-09-20
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-20     Baekgwa               Initial creation
 */
public record MaterialHistoryEvent(
	Long projectId,
	Long userId,
	List<MaterialHistoryEventDto> historyList
) {
}
