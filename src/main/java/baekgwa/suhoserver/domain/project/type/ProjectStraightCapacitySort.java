package baekgwa.suhoserver.domain.project.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.type
 * FileName    : ProjectStraightCapacitySort
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
public enum ProjectStraightCapacitySort {

	CAPACITY("생산 가능"),
	LENGTH("길이"),
	TOTAL_QUANTITY("제작 수량");

	private final String description;
}
