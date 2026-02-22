package baekgwa.suhoserver.infra.history.event;

import baekgwa.suhoserver.model.material.MaterialHistoryType;

/**
 * PackageName : baekgwa.suhoserver.infra.history.event
 * FileName    : MaterialHistoryEventDto
 * Author      : Baekgwa
 * Date        : 2025-09-20
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-20     Baekgwa               Initial creation
 */
public record MaterialHistoryEventDto(
	Long projectMaterialStockId,
	Long quantity,
	MaterialHistoryType type
) {
}
