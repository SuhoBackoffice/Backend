package baekgwa.suhoserver.domain.branch.dto;

import java.time.LocalDate;
import java.util.List;

import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.branch.dto
 * FileName    : BranchResponse
 * Author      : Baekgwa
 * Date        : 2025-08-05
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-05     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BranchResponse {

	@Getter
	public static class BranchInfoDto {
		private final Long branchTypeId;
		private final String versionName;
		private final Long versionId;
		private final String branchCode;
		private final LocalDate version;
		private final List<BranchDetailInfoDto> branchDetailinfoDtoList;

		@Builder(access = AccessLevel.PRIVATE)
		private BranchInfoDto(Long branchTypeId, String versionName, Long versionId, String branchCode, LocalDate version,
			List<BranchDetailInfoDto> branchDetailinfoDtoList) {
			this.branchTypeId = branchTypeId;
			this.versionName = versionName;
			this.versionId = versionId;
			this.branchCode = branchCode;
			this.version = version;
			this.branchDetailinfoDtoList = branchDetailinfoDtoList;
		}

		public static BranchInfoDto from(BranchTypeEntity branchTypeEntity, List<BranchDetailInfoDto> branchDetailinfoDtoList) {
			return BranchInfoDto
				.builder()
				.branchTypeId(branchTypeEntity.getId())
				.versionName(branchTypeEntity.getVersionInfoEntity().getName())
				.versionId(branchTypeEntity.getVersionInfoEntity().getId())
				.branchCode(branchTypeEntity.getCode())
				.version(branchTypeEntity.getVersion())
				.branchDetailinfoDtoList(branchDetailinfoDtoList)
				.build();
		}
	}

	@Getter
	public static class BranchDetailInfoDto {
		private final Long branchBomId;
		private final String itemType;
		private final String drawingNumber;
		private final String itemName;
		private final String specification;
		private final Long unitQuantity;
		private final String unit;
		private final Boolean suppliedMaterial;

		@Builder(access = AccessLevel.PRIVATE)
		private BranchDetailInfoDto(Long branchBomId, String itemType, String drawingNumber, String itemName,
			String specification, Long unitQuantity, String unit, Boolean suppliedMaterial) {
			this.branchBomId = branchBomId;
			this.itemType = itemType;
			this.drawingNumber = drawingNumber;
			this.itemName = itemName;
			this.specification = specification;
			this.unitQuantity = unitQuantity;
			this.unit = unit;
			this.suppliedMaterial = suppliedMaterial;
		}

		public static BranchDetailInfoDto of(BranchBomEntity branchBomEntity) {
			return BranchDetailInfoDto
				.builder()
				.branchBomId(branchBomEntity.getId())
				.itemType(branchBomEntity.getItemType())
				.drawingNumber(branchBomEntity.getDrawingNumber())
				.itemName(branchBomEntity.getItemName())
				.specification(branchBomEntity.getSpecification())
				.unitQuantity(branchBomEntity.getUnitQuantity())
				.unit(branchBomEntity.getUnit())
				.suppliedMaterial(branchBomEntity.getSuppliedMaterial())
				.build();
		}
	}

	@Getter
	public static class PostNewBranchBom {
		private final Long branchTypeId;

		public PostNewBranchBom(Long branchTypeId) {
			this.branchTypeId = branchTypeId;
		}
	}
}
