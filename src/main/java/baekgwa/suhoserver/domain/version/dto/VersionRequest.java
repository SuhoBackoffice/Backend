package baekgwa.suhoserver.domain.version.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.version.dto
 * FileName    : VersionRequest
 * Author      : Baekgwa
 * Date        : 2025-08-05
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-05     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionRequest {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class NewVersionDto {
		@NotNull(message = "버전 이름은 필수 입니다.")
		private String versionName;

		@DecimalMin(value = "150.0", inclusive = true, message = "루프 리치 서포트의 길이는 최소 150mm 입니다.")
		@Digits(integer = 3, fraction = 1, message = "정수부 최대 3자리, 소수부 최대 1자리까지 입력 가능합니다.")
		private BigDecimal loopLitzWire;
	}
}
