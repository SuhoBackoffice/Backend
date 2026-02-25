package baekgwa.suhoserver.infra.history.event;

/**
 * PackageName : baekgwa.suhoserver.infra.history.event
 * FileName    : ProjectBranchUpdatedEvent
 * Author      : Baekgwa
 * Date        : 25. 12. 27.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 27.     Baekgwa               Initial creation
 */
public record ProjectBranchUpdatedEvent(
	Long projectId,
	Long userId,
	Long projectBranchId,
	Long beforeQuantity,
	Long afterQuantity,
	String code
) {
}
