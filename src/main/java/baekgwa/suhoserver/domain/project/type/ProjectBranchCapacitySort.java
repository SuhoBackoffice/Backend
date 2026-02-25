package baekgwa.suhoserver.domain.project.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.type
 * FileName    : ProjectBranchCapacitySort
 * Author      : Baekgwa
 * Date        : 26. 2. 24.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 24.     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum ProjectBranchCapacitySort {

	CAPACITY("생산 가능"),
	CODE("코드 번호"),
	TOTAL_QUANTITY("제작 수량");

	private final String description;
}
