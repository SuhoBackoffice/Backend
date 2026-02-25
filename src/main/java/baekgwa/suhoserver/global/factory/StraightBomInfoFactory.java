package baekgwa.suhoserver.global.factory;

import java.math.BigDecimal;

import lombok.experimental.UtilityClass;

/**
 * PackageName : baekgwa.suhoserver.global.factory
 * FileName    : StraightBomInfoFactory
 * Author      : Baekgwa
 * Date        : 26. 2. 21.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 21.     Baekgwa               Initial creation
 */
@UtilityClass
public class StraightBomInfoFactory {

	/**
	 * 도번 생성
	 * ST-${length}L
	 * ST-${length}L-hole
	 * ST-2400L-1200
	 * @param length
	 * @param holePosition
	 * @return
	 */
	public static String generateProfileMaterialCode(Long length, BigDecimal holePosition) {
		StringBuilder sb = new StringBuilder();
		sb.append("ST-").append(length).append("L");
		if(holePosition != null && !holePosition.equals(BigDecimal.ZERO)) {
			sb.append("-").append(holePosition.doubleValue());
		}

		return sb.toString();
	}

	/**
	 * 품명 생성
	 * [${version}]Profile ${length}L 레일용
	 * [${version}]Profile ${length}L 루프레일용
	 * @param length
	 * @param isLoopRail
	 * @param versionName
	 * @return
	 */
	public static String generateProfileItemName(Long length, Boolean isLoopRail, String versionName) {
		StringBuilder sb = new StringBuilder();
		sb
			.append("[").append(versionName).append("]")
			.append("Profile ")
			.append(length).append("L");
		sb.append(Boolean.TRUE.equals(isLoopRail) ? " 루프레일용" : " 레일용");
		return sb.toString();
	}

	/**
	 * 코드 생성 (Litzwire)
	 * LW-2400L
	 * @param litzwire
	 * @return
	 */
	public static String generateLitzwireMaterialCode(BigDecimal litzwire) {
		StringBuilder sb = new StringBuilder();
		sb.append("LW-").append(litzwire.doubleValue()).append("L");

		return sb.toString();
	}

	/**
	 * 리츠와이어 이름 생성 (Litzwire)
	 * @param versionName
	 * @param litzwire
	 * @return
	 */
	public static String generateLitzwireItemName(String versionName, BigDecimal litzwire) {
		StringBuilder sb = new StringBuilder();
		sb
			.append("[").append(versionName).append("]")
			.append("Litzwire SPT ")
			.append(litzwire.doubleValue()).append("L");

		return sb.toString();
	}
}
