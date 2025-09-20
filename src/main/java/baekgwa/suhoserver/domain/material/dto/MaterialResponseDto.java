package baekgwa.suhoserver.domain.material.dto;

import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.material.dto
 * FileName    : MaterialResponseDto
 * Author      : Baekgwa
 * Date        : 2025-09-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-19     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaterialResponseDto {

	@Getter
	public static class MaterialInfo {
		private final String drawingNumber;
		private final String materialName;

		@Builder(access = AccessLevel.PRIVATE)
		private MaterialInfo(String drawingNumber, String materialName) {
			this.drawingNumber = drawingNumber;
			this.materialName = materialName;
		}

		public static MaterialInfo of(BranchBomEntity branchBomEntity) {
			return MaterialInfo
				.builder()
				.drawingNumber(branchBomEntity.getDrawingNumber())
				.materialName(branchBomEntity.getItemName())
				.build();
		}
	}
}
