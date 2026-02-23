package baekgwa.suhoserver.domain.straight.dto;

import baekgwa.suhoserver.model.project.straight.bom.entity.ProjectStraightBomEntity;
import baekgwa.suhoserver.model.straight.type.entity.StraightTypeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.straight.dto
 * FileName    : StraightResponse
 * Author      : Baekgwa
 * Date        : 2025-08-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-10     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StraightResponse {

	@Getter
	public static class StraightTypeDto {
		private final Long id;
		private final String type;

		private StraightTypeDto(Long id, String type) {
			this.id = id;
			this.type = type;
		}

		public static StraightTypeDto from(StraightTypeEntity straightType) {
			return new StraightTypeDto(straightType.getId(), straightType.getType());
		}
	}

	@Getter
	public static class StraightBom {
		private final Long straightBomId;
		private final String materialCode;
		private final String itemName;
		private final Long unitQuantity;

		@Builder(access = AccessLevel.PRIVATE)
		private StraightBom(Long straightBomId, String materialCode, String itemName, Long unitQuantity) {
			this.straightBomId = straightBomId;
			this.materialCode = materialCode;
			this.itemName = itemName;
			this.unitQuantity = unitQuantity;
		}

		public static StraightBom from(ProjectStraightBomEntity straightBom) {
			return StraightBom
				.builder()
				.straightBomId(straightBom.getId())
				.materialCode(straightBom.getMaterialCode())
				.itemName(straightBom.getItemName())
				.unitQuantity(straightBom.getUnitQuantity())
				.build();
		}
	}
}
