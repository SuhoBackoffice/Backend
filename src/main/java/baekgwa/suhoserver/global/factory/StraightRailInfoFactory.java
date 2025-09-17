package baekgwa.suhoserver.global.factory;

import static java.math.RoundingMode.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import lombok.experimental.UtilityClass;

/**
 * PackageName : baekgwa.suhoserver.global.factory
 * FileName    : StraightRailInfoFactory
 * Author      : Baekgwa
 * Date        : 2025-09-17
 * Description : 직선레일에 들어갈 Litzwire 정보를, 생성하는 유틸 클래스
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-17     Baekgwa               Initial creation
 */
@UtilityClass
public class StraightRailInfoFactory {
	private static final BigDecimal LITZ_WIRE_MAX = BigDecimal.valueOf(1800L);
	private static final BigDecimal OFFSET_215 = BigDecimal.valueOf(215L);
	private static final BigDecimal TH_1200 = BigDecimal.valueOf(1200L);
	private static final BigDecimal TH_2400 = BigDecimal.valueOf(2400L);
	private static final BigDecimal TH_3600 = BigDecimal.valueOf(3600L);

	/**
	 * 직선레일의 가공 위치를 계산하는 메서드
	 * @param length 총 길이
	 * @param type 레일 타입 정보
	 * @param isLoopRail 루프 레일 여부
	 * @param loopLitzWire 루프 리츠와이어 길이
	 * @return 가공 위치
	 */
	public static BigDecimal calculateHolePosition(Long length, String type, boolean isLoopRail,
		BigDecimal loopLitzWire) {
		if (!isLoopRail) {
			return BigDecimal.ZERO;
		}

		String loopType = type.substring(0, 1).toUpperCase();

		return switch (loopType) {
			case "C" -> BigDecimal.valueOf(length)
				.divide(BigDecimal.valueOf(2));
			case "E" -> BigDecimal.valueOf(length)
				.subtract(loopLitzWire); // End: length - loopLitzWire
			case "S" -> BigDecimal.valueOf(length)
				.subtract(BigDecimal.valueOf(215))
				.subtract(loopLitzWire); // ✅ Side: length - 215 - loopLitzWire
			default -> BigDecimal.ZERO;
		};
	}

	/**
	 * 직선레일 리츠와이어 정보
	 * 없는 리츠와이어는 0 으로 표기
	 * @param length 총 길이
	 * @param type 레일 타입 정보
	 * @param isLoopRail 루프 레일 여부
	 * @return 리츠와이어 1~6 까지의 정보 배열
	 */
	public static BigDecimal[] generateLitzWire(Long length, String type, boolean isLoopRail, BigDecimal loopLitzWire) {
		// 1. type 을 Upper로 통일
		String rawType = type.toUpperCase();

		// 2. 일반/루프 나눠서 타입 정리
		String offsetType;
		String loopType;
		if (isLoopRail) {
			if (rawType.length() >= 2) {
				// "CA" -> "A", "EC" -> "C"
				loopType = rawType.substring(0, 1);
				offsetType = rawType.substring(1, 2);
			} else {
				throw new GlobalException(ErrorCode.INVALID_LOOP_RAIL_TYPE_DATA);
			}
		} else {
			loopType = null;
			offsetType = rawType;
		}

		// 3. baseLitzWire 계산
		Map<Integer, BigDecimal> baseLitzWireMap = isLoopRail
			? baseLoopLitzWireSupporter(length, loopType, loopLitzWire)
			: baseLitzWireSupporter(length);

		// 4. 타입에 따라, LitzWire 추가 계산
		//		B 타입의 경우, 좌측 위가 215mm 비어있음.
		Map<LitzWireCorner, Integer> anchorMap = pickAnchors(baseLitzWireMap);

		switch (offsetType) {
			case "B" -> dec(baseLitzWireMap, anchorMap.get(LitzWireCorner.LU));
			case "C" -> dec(baseLitzWireMap, anchorMap.get(LitzWireCorner.LD));
			case "D" -> {
				dec(baseLitzWireMap, anchorMap.get(LitzWireCorner.LU));
				dec(baseLitzWireMap, anchorMap.get(LitzWireCorner.RU));
			}
			case "E" -> {
				dec(baseLitzWireMap, anchorMap.get(LitzWireCorner.LU));
				dec(baseLitzWireMap, anchorMap.get(LitzWireCorner.RD));
			}
			case "F" -> {
				dec(baseLitzWireMap, anchorMap.get(LitzWireCorner.LD));
				dec(baseLitzWireMap, anchorMap.get(LitzWireCorner.RU));
			}
			case "G" -> {
				dec(baseLitzWireMap, anchorMap.get(LitzWireCorner.LU));
				dec(baseLitzWireMap, anchorMap.get(LitzWireCorner.LD));
			}
			case "A" -> { /* no-op */ }
			default -> { /* 변경 없음 */ }
		}

		return new BigDecimal[] {baseLitzWireMap.get(1), baseLitzWireMap.get(2), baseLitzWireMap.get(3),
			baseLitzWireMap.get(4), baseLitzWireMap.get(5), baseLitzWireMap.get(6)};
	}

	/**
	 * 일반 레일, 베이스 LitzWire 계산
	 * @param length 직선레일 총 길이
	 * @return litzWireMap <리츠와이어 번호, 리츠와이어 길이>
	 */
	private Map<Integer, BigDecimal> baseLitzWireSupporter(long length) {
		BigDecimal len = BigDecimal.valueOf(length);
		Map<Integer, BigDecimal> litzWireMap = new HashMap<>();

		// LITZ_WIRE_MAX (1800) 보다 작을경우, 1, 2번 리츠와이어만 채우면 됨.
		if (len.compareTo(LITZ_WIRE_MAX) <= 0) {
			litzWireMap.put(1, len);
			litzWireMap.put(2, len);
		}
		// TH_2400(2400) 보다 짧으면, 1, 2번은, 1200으로 채우고, 나머지를 3, 4에 배치
		else if (len.compareTo(TH_2400) <= 0) {
			BigDecimal rest = len.subtract(TH_1200);
			litzWireMap.put(1, TH_1200);
			litzWireMap.put(2, TH_1200);
			litzWireMap.put(3, rest);
			litzWireMap.put(4, rest);
		}
		// TH_3600(3600) 보다 짧으면, 1, 2번은, 1800으로, 나머지는 3, 4에 배치
		// 일반 레일은 어떤 경우에도, 5, 6번 리츠와이어가 있는 경우는 없음.
		else if (len.compareTo(TH_3600) <= 0) { // <= 3600
			BigDecimal rest = len.subtract(LITZ_WIRE_MAX);
			litzWireMap.put(1, LITZ_WIRE_MAX);
			litzWireMap.put(2, LITZ_WIRE_MAX);
			litzWireMap.put(3, rest);
			litzWireMap.put(4, rest);
		} else {
			throw new GlobalException(ErrorCode.INVALID_STRAIGHT_RAIL_LENGTH);
		}
		return litzWireMap;
	}

	/**
	 * 루프 레일, 베이스 LitzWire 계산
	 * @param length 레일 총 길이
	 * @param loopType 루프레일 타입 [C = Center], [S = Side], [E = End]
	 * @param loopLitzWire 루프 리츠와이어 사출품 길이 (버전마다 다름)
	 * @return litzWireMap <리츠와이어 번호, 리츠와이어 길이>
	 */
	private Map<Integer, BigDecimal> baseLoopLitzWireSupporter(
		Long length, String loopType, BigDecimal loopLitzWire
	) {
		BigDecimal len = BigDecimal.valueOf(length);
		BigDecimal loop = loopLitzWire;
		Map<Integer, BigDecimal> litzWireMap = new HashMap<>();

		switch (loopType) {
			case "C" -> {
				BigDecimal perSide = len.subtract(loop.multiply(BigDecimal.valueOf(2L)))
					.divide(BigDecimal.valueOf(2L), 1, HALF_UP);
				perSide = perSide.max(BigDecimal.ZERO);
				litzWireMap.put(1, perSide);
				litzWireMap.put(2, perSide);
				litzWireMap.put(3, perSide);
				litzWireMap.put(4, perSide);
			}
			case "E" -> {
				BigDecimal leftBase;
				if (len.compareTo(LITZ_WIRE_MAX) <= 0) {
					leftBase = len;
				} else if (len.compareTo(TH_2400) <= 0) {
					leftBase = TH_1200;
				} else {
					leftBase = LITZ_WIRE_MAX;
				}
				BigDecimal remaining = len.subtract(leftBase).max(BigDecimal.ZERO);
				BigDecimal right = remaining.subtract(loop.multiply(BigDecimal.valueOf(2L))).max(BigDecimal.ZERO);
				litzWireMap.put(1, leftBase);
				litzWireMap.put(2, leftBase);
				if (right.signum() > 0) {
					litzWireMap.put(3, right);
					litzWireMap.put(4, right);
				}
			}
			case "S" -> {
				BigDecimal leftBase = len.min(LITZ_WIRE_MAX);
				BigDecimal remaining = len.subtract(leftBase);
				BigDecimal deduct = loop.multiply(BigDecimal.valueOf(2L)).add(OFFSET_215);

				if (remaining.signum() > 0) {
					BigDecimal right = remaining.subtract(deduct).max(BigDecimal.ZERO);
					litzWireMap.put(1, leftBase);
					litzWireMap.put(2, leftBase);
					litzWireMap.put(3, right);
					litzWireMap.put(4, right);
					litzWireMap.put(5, OFFSET_215);
					litzWireMap.put(6, OFFSET_215);
				} else {
					BigDecimal left = leftBase.subtract(deduct).max(BigDecimal.ZERO);
					litzWireMap.put(1, left);
					litzWireMap.put(2, left);
					litzWireMap.put(3, OFFSET_215);
					litzWireMap.put(4, OFFSET_215);
				}
			}
			default -> {
				return baseLitzWireSupporter(length);
			}
		}
		return litzWireMap;
	}

	/**
	 * 차감할 리츠와이어 좌측 위/아래, 우측 위/아래가 각각 몇번 리츠와이어인지 확인하는 메서드
	 * @param baseLitzWire 계산된 base LitzWire 정보
	 * @return Map<LitzWireCorner, 리츠와이어 번호>
	 */
	private Map<LitzWireCorner, Integer> pickAnchors(Map<Integer, BigDecimal> baseLitzWire) {
		int ru;
		if (present(baseLitzWire, 5)) {
			ru = 5;
		} else if (present(baseLitzWire, 3)) {
			ru = 3;
		} else {
			ru = 1;
		}

		int rd;
		if (present(baseLitzWire, 6)) {
			rd = 6;
		} else if (present(baseLitzWire, 4)) {
			rd = 4;
		} else {
			rd = 2;
		}

		return Map.of(
			LitzWireCorner.LU, 1,
			LitzWireCorner.LD, 2,
			LitzWireCorner.RU, ru,
			LitzWireCorner.RD, rd
		);
	}

	/**
	 * 유무 확인용
	 * @param litzWireMap 리츠와이어 정보 담긴 Map<리츠와이어 번호, 리츠와이어 길이>
	 * @param idx 리츠와이어 번호
	 * @return
	 */
	private boolean present(Map<Integer, BigDecimal> litzWireMap, int idx) {
		return litzWireMap.getOrDefault(idx, BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0;
	}

	/**
	 * 해당 인덱스, 215mm 차감 진행
	 * @param litzWireMap 리츠와이어 정보 담긴 Map<리츠와이어 번호, 리츠와이어 길이>
	 * @param idx 리츠와이어 번호
	 */
	private void dec(Map<Integer, BigDecimal> litzWireMap, int idx) {
		BigDecimal v = litzWireMap.getOrDefault(idx, BigDecimal.ZERO);
		v = v.subtract(OFFSET_215).max(BigDecimal.ZERO);
		litzWireMap.put(idx, v);
	}
}