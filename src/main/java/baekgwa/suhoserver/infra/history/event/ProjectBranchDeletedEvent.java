package baekgwa.suhoserver.infra.history.event;

/**
 * PackageName : baekgwa.suhoserver.infra.history.event
 * FileName    : ProjectBranchDeletedEvent
 * Author      : Baekgwa
 * Date        : 25. 12. 27.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 27.     Baekgwa               Initial creation
 */
public record ProjectBranchDeletedEvent(
	Long projectId,
	Long userId,
	Long projectBranchId,
	Long beforeQuantity,
	String code
) {
}
