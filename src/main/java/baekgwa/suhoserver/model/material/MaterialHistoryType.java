package baekgwa.suhoserver.model.material;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.material
 * FileName    : MaterialHistoryType
 * Author      : Baekgwa
 * Date        : 26. 2. 18.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 18.     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum MaterialHistoryType {

	INBOUND("입고"),
	OUTBOUND("반출"),
	PRODUCTION_USE("사용");

	private final String description;
}
