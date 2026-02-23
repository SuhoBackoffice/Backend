package baekgwa.suhoserver.domain.material.dto;

import baekgwa.suhoserver.domain.material.type.MaterialSort;
import baekgwa.suhoserver.model.material.MaterialHistoryType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
	public static class GetMaterialHistory {
		final String keyword;
		final MaterialHistoryType type;
		final MaterialSort sort;
		final int page;
		final int size;

		public GetMaterialHistory(String keyword, MaterialHistoryType type, MaterialSort sort, int page, int size) {
			this.keyword = keyword;
			this.type = type;
			this.sort = sort;
			this.page = page;
			this.size = size;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class PostMaterialInbound {
		@NotNull(message = "제품 정보는 필수 입니다.")
		private Long projectMaterialStockId;
		@Min(value = 1L, message = "입고 수량은 최소 1개 입니다.")
		private Long quantity;
	}
}
