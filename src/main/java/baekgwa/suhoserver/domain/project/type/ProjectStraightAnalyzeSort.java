package baekgwa.suhoserver.domain.project.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.type
 * FileName    : ProjectStraightAnalyzeSort
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
public enum ProjectStraightAnalyzeSort {

	SHORTAGE_QUANTITY("부족 수량"),
	MATERIAL_CODE("도번"),
	ITEM_NAME("품명");

	private final String description;
}
