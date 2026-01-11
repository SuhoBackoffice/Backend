package baekgwa.suhoserver.model.project;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.project
 * FileName    : ProductProductionState
 * Author      : Baekgwa
 * Date        : 25. 12. 28.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 28.     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum ProductProductionState {

	NOT_PRODUCED("미생산"),
	PRODUCED("생산 완료");

	private final String description;
}
