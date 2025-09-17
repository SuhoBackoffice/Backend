package baekgwa.suhoserver.global.factory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.service
 * FileName    : LitzWireCorner
 * Author      : Baekgwa
 * Date        : 2025-08-11
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-11     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum LitzWireCorner {
	LU("좌측 상단"),
	LD("좌측 하단"),
	RU("우측 상단"),
	RD("우측 하단");

	private final String description;
}
