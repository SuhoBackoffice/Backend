package baekgwa.suhoserver.infra.history.event;

import java.util.List;

/**
 * PackageName : baekgwa.suhoserver.infra.history.event
 * FileName    : ProjectBranchCreatedEvent
 * Author      : Baekgwa
 * Date        : 25. 12. 26.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 26.     Baekgwa               Initial creation
 */
public record ProjectBranchCreatedEvent(
	Long projectId,
	Long userId,
	List<ProjectBranchCreatedEventDto> projectBranchDtoList
) {
}
