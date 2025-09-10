package baekgwa.suhoserver.infra.upload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.infra.upload
 * FileName    : FileType
 * Author      : Baekgwa
 * Date        : 2025-09-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-10     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum FileType {

	BRANCH_IMAGE("branch"),
	;

	private final String path;
}

