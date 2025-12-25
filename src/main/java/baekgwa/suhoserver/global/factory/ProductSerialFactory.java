package baekgwa.suhoserver.global.factory;

import lombok.experimental.UtilityClass;

/**
 * PackageName : baekgwa.suhoserver.global.factory
 * FileName    : ProductSerialFactory
 * Author      : Baekgwa
 * Date        : 25. 12. 23.
 * Description : 제품군에 부착될 시리얼 번호를 생성하는 메서드
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 23.     Baekgwa               Initial creation
 */
@UtilityClass
public class ProductSerialFactory {

	/**
	 * 직선레일의 식별 시리얼 번호 생성 팩토리
	 * @param length 길이
	 * @param isLoop 루프 여부
	 * @param straightType 레일 타입
	 * @param sequence 순서
	 * @return 식별용 시리얼 번호
	 */
	public static String generateStraightSerial(Long length, Boolean isLoop, String straightType, long sequence) {
		String prefix = isLoop == Boolean.FALSE ? "SR" : "LR";
		String seq = String.format("%02d", sequence);
		return prefix + length + straightType + "-" + seq;
	}

	/**
	 * 직선레일의 식별 시리얼 번호 생성 팩토리
	 * @param length 길이
	 * @param isLoop 루프 여부
	 * @param straightType 레일 타입
	 * @return 식별용 시리얼 번호
	 */
	public static String generateStraightSerial(Long length, Boolean isLoop, String straightType) {
		String prefix = isLoop == Boolean.FALSE ? "SR" : "LR";
		return prefix + length + straightType;
	}

	/**
	 * 분기레일 식별 시리얼 번호 생성 팩토리
	 * @param code 분기레일 코드
	 * @param sequence 순서
	 * @return 식별용 시리얼 번호 ex)B401-01
	 */
	public static String generateBranchSerial(String code, long sequence) {
		String seq = String.format("%02d", sequence);
		return "B" + code + "-" + seq;
	}
}
