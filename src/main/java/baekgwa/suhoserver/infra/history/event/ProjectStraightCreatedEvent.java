package baekgwa.suhoserver.infra.history.event;

import java.util.List;

/**
 * PackageName : baekgwa.suhoserver.infra.history.event
 * FileName    : ProjectStraightCreatedEvent
 * Author      : Baekgwa
 * Date        : 25. 12. 26.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 26.     Baekgwa               Initial creation
 */
public record ProjectStraightCreatedEvent(
	Long projectId,
	Long userId,
	List<ProjectStraightCreatedEventDto> projectStraightList
) {
}
