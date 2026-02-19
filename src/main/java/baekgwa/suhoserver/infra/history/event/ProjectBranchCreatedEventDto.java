package baekgwa.suhoserver.infra.history.event;

/**
 * PackageName : baekgwa.suhoserver.infra.history.event
 * FileName    : ProjectBranchCreatedEventDto
 * Author      : Baekgwa
 * Date        : 25. 12. 26.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 26.     Baekgwa               Initial creation
 */
public record ProjectBranchCreatedEventDto(
	Long projectBranchId,
	Long afterQuantity,
	String code
) {
}
