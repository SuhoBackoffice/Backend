package baekgwa.suhoserver.domain.material.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.material.type
 * FileName    : MaterialSort
 * Author      : Baekgwa
 * Date        : 2025-09-20
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-20     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum MaterialSort {
	LATEST("최신 순"),
	OLDEST("오래된 순");

	private final String description;
}
