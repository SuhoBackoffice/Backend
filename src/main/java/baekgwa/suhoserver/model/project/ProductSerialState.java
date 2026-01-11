package baekgwa.suhoserver.model.project;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.project
 * FileName    : ProductSerialState
 * Author      : Baekgwa
 * Date        : 25. 12. 23.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 23.     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum ProductSerialState {

	ACTIVE("유효"),
	INACTIVE("생산 취소");

	private final String description;
}
