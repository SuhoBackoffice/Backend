package baekgwa.suhoserver.infra.history.event;

/**
 * PackageName : baekgwa.suhoserver.infra.history.event
 * FileName    : ProjectStraightUpdatedEvent
 * Author      : Baekgwa
 * Date        : 25. 12. 26.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 26.     Baekgwa               Initial creation
 */
public record ProjectStraightUpdatedEvent(
	Long projectId,
	Long userId,
	Long projectStraightId,
	Long length,
	Boolean isLoopRail,
	String straightType,
	Long beforeQuantity,
	Long afterQuantity
) {
}
