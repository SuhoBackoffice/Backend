package baekgwa.suhoserver.model.project;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.project
 * FileName    : ProductInactiveReason
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
public enum ProductInactiveReason {

	DESIGN_CHANGE("설계 변경");

	private final String description;
}
