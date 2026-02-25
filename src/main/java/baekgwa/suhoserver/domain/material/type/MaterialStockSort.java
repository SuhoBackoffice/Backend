package baekgwa.suhoserver.domain.material.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.material.type
 * FileName    : MaterialStockSort
 * Author      : Baekgwa
 * Date        : 2026-02-25
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-02-25     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum MaterialStockSort {
	MATERIAL_CODE("도번"),
	ITEM_NAME("품명"),
	PLAN_QUANTITY("계획 수량"),
	INBOUND_QUANTITY("입고 수량"),
	USED_QUANTITY("사용 수량");

	private final String description;
}
