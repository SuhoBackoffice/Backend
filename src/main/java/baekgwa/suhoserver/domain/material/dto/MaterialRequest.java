package baekgwa.suhoserver.domain.material.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.material.dto
 * FileName    : MaterialRequest
 * Author      : Baekgwa
 * Date        : 2025-09-20
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-20     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaterialRequest {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class PostMaterialInbound {
		@NotBlank(message = "부품 코드는 필수 입니다.")
		private String drawingNumber;
		@NotBlank(message = "부품 명은 필수 입니다.")
		private String itemName;
		@Min(value = 1L, message = "입고 수량은 최소 1개 입니다.")
		private Long quantity;
	}
}
