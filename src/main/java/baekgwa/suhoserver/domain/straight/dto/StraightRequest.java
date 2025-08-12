package baekgwa.suhoserver.domain.straight.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.straight.dto
 * FileName    : StraightRequest
 * Author      : Baekgwa
 * Date        : 2025-08-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-10     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StraightRequest {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class NewStraightTypeDto {
		@NotBlank(message = "직선레일 타입은 필수 입니다.")
		private String type;
		@NotNull(message = "루프 여부는 필수 입니다.")
		private Boolean isLoopRail;
	}
}
