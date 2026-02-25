package baekgwa.suhoserver.model.straight;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.straight
 * FileName    : StraightBomConditionType
 * Author      : Baekgwa
 * Date        : 26. 2. 20.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 20.     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum StraightBomConditionType {
	YOKE("요크 관련"),
	LITZ_WIRE("리츠 와이어 관련"),
	LOOP_LITZ_WIRE("루프 리츠와이어 관련"),
	FIXED("조건 없음");

	private final String description;
}