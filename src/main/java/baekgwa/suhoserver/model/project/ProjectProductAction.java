package baekgwa.suhoserver.model.project;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.project
 * FileName    : ProjectProductAction
 * Author      : Baekgwa
 * Date        : 25. 12. 26.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 26.     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum ProjectProductAction {

	CREATED("생성"),
	UPDATED("업데이트"),
	DELETED("삭제");

	private final String description;
}
