package baekgwa.suhoserver.domain.material.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import baekgwa.suhoserver.model.material.MaterialHistoryType;
import baekgwa.suhoserver.model.material.history.entity.MaterialHistoryEntity;
import baekgwa.suhoserver.model.material.project.entity.ProjectMaterialStockEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.material.dto
 * FileName    : MaterialResponse
 * Author      : Baekgwa
 * Date        : 2025-09-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-19     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaterialResponse {

	@Getter
	public static class MaterialHistoryTypeInfo {
		private final String code;
		private final String description;

		private MaterialHistoryTypeInfo(String code, String description) {
			this.code = code;
			this.description = description;
		}

		public static MaterialHistoryTypeInfo from(MaterialHistoryType type) {
			return new MaterialHistoryTypeInfo(type.name(), type.getDescription());
		}
	}

	@Getter
	public static class MaterialHistoryInfo {
		private final Long id;
		private final String materialCode;
		private final String itemName;
		private final Long quantity;
		private final String description;
		private final String type;
		private final LocalDateTime createdAt;

		@Builder(access = AccessLevel.PRIVATE)
		private MaterialHistoryInfo(Long id, String materialCode, String itemName, Long quantity,
			String description, String type, LocalDateTime createdAt) {
			this.id = id;
			this.materialCode = materialCode;
			this.itemName = itemName;
			this.quantity = quantity;
			this.description = description;
			this.type = type;
			this.createdAt = createdAt;
		}

		public static MaterialHistoryInfo of(MaterialHistoryEntity history) {
			return MaterialHistoryInfo.builder()
				.id(history.getId())
				.materialCode(history.getMaterialCode())
				.itemName(history.getItemName())
				.quantity(history.getQuantity())
				.description(history.getDescription())
				.type(history.getType().getDescription())
				.createdAt(history.getCreatedAt())
				.build();
		}
	}

	@Getter
	public static class SearchMaterialInfo {
		private final Long id;
		private final String drawingNumber;
		private final String itemName;
		private final Long needInboundQuantity;

		@Builder(access = AccessLevel.PRIVATE)
		public SearchMaterialInfo(Long id, String drawingNumber, String itemName, Long needInboundQuantity) {
			this.id = id;
			this.drawingNumber = drawingNumber;
			this.itemName = itemName;
			this.needInboundQuantity = needInboundQuantity;
		}

		public static SearchMaterialInfo from(ProjectMaterialStockEntity stock) {

			Long needInboundQuantity = stock.getTotalPlanQuantity() - stock.getTotalInboundQuantity();

			return SearchMaterialInfo
				.builder()
				.id(stock.getId())
				.drawingNumber(stock.getMaterialCode())
				.itemName(stock.getItemName())
				.needInboundQuantity(needInboundQuantity)
				.build();
		}
	}

	@Getter
	public static class ProjectMaterialState {
		private final BigDecimal inboundPercent; // 입고 진행률
		private final Long unitKindCount; // 자재 총 종류
		private final Long totalCount; // 자재 총 수량
		private final Long inboundCount; // 입고 총 수량
		private final Long usedCount; // 제작에 사용 된 총 수량

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectMaterialState(BigDecimal inboundPercent, Long unitKindCount, Long totalCount, Long inboundCount,
			Long usedCount) {
			this.inboundPercent = inboundPercent;
			this.unitKindCount = unitKindCount;
			this.totalCount = totalCount;
			this.inboundCount = inboundCount;
			this.usedCount = usedCount;
		}

		public static ProjectMaterialState from(
			BigDecimal inboundPercent, Long unitKindCount, Long totalCount, Long inboundCount, Long usedCount
		) {
			return ProjectMaterialState
				.builder()
				.inboundPercent(inboundPercent)
				.unitKindCount(unitKindCount)
				.totalCount(totalCount)
				.inboundCount(inboundCount)
				.usedCount(usedCount)
				.build();
		}
	}
}
