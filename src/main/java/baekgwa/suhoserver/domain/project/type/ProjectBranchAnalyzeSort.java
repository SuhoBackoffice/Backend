package baekgwa.suhoserver.domain.project.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.type
 * FileName    : ProjectBranchAnalyzeSort
 * Author      : Baekgwa
 * Date        : 2026-02-24
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-02-24     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum ProjectBranchAnalyzeSort {
	SHORTAGE_QUANTITY("부족 수량"),
	DRAWING_NUMBER("도번"),
	ITEM_NAME("품명");

	private final String description;
}
