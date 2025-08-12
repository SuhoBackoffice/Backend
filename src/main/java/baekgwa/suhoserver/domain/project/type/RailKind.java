package baekgwa.suhoserver.domain.project.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.type
 * FileName    : RailKind
 * Author      : Baekgwa
 * Date        : 2025-08-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-10     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum RailKind {
	NORMAL(false),
	LOOP(true);

	private final boolean loop;
}
