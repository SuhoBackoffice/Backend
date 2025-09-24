package baekgwa.suhoserver.domain.material.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.material.inbound.entity.MaterialInboundEntity;
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
	public static class MaterialInfo {
		private final String drawingNumber;
		private final String itemName;

		@Builder(access = AccessLevel.PRIVATE)
		private MaterialInfo(String drawingNumber, String itemName) {
			this.drawingNumber = drawingNumber;
			this.itemName = itemName;
		}

		public static MaterialInfo of(BranchBomEntity branchBomEntity) {
			return MaterialInfo
				.builder()
				.drawingNumber(branchBomEntity.getDrawingNumber())
				.itemName(branchBomEntity.getItemName())
				.build();
		}
	}

	@Getter
	public static class MaterialHistory {
		private final LocalDate date;
		private final Long kindCount;
		private final Long totalCount;

		@Builder(access = AccessLevel.PRIVATE)
		public MaterialHistory(LocalDate date, Long kindCount, Long totalCount) {
			this.date = date;
			this.kindCount = kindCount;
			this.totalCount = totalCount;
		}

		public static MaterialHistory of(LocalDate date, Long kindCount, Long totalCount) {
			return MaterialHistory.builder().date(date).kindCount(kindCount).totalCount(totalCount).build();
		}
	}

	@Getter
	public static class MaterialHistoryDetail {
		private final Long id;
		private final String drawingNumber;
		private final String itemName;
		private final LocalDateTime receivedAt;
		private final Long quantity;

		@Builder(access = AccessLevel.PRIVATE)
		private MaterialHistoryDetail(Long id, String drawingNumber, String itemName, LocalDateTime receivedAt, Long quantity) {
			this.id = id;
			this.drawingNumber = drawingNumber;
			this.itemName = itemName;
			this.receivedAt = receivedAt;
			this.quantity = quantity;
		}

		public static MaterialHistoryDetail of(MaterialInboundEntity materialInbound) {
			return MaterialHistoryDetail
				.builder()
				.id(materialInbound.getId())
				.drawingNumber(materialInbound.getDrawingNumber())
				.itemName(materialInbound.getItemName())
				.receivedAt(materialInbound.getCreatedAt())
				.quantity(materialInbound.getQuantity())
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

		public static ProjectMaterialState from(Long unitKindCount, Long totalCount, Long usedCount) {
			return ProjectMaterialState
				.builder()
				.unitKindCount(unitKindCount)
				.totalCount(totalCount)
				.usedCount(usedCount)
				.build();
		}

		public static ProjectMaterialState from(ProjectMaterialState materialState, Long inboundCount) {
			return ProjectMaterialState
				.builder()
				.inboundPercent(BigDecimal.valueOf(inboundCount).multiply(BigDecimal.valueOf(100))
					.divide(BigDecimal.valueOf(materialState.getTotalCount()), 1, RoundingMode.HALF_UP))
				.unitKindCount(materialState.getUnitKindCount())
				.totalCount(materialState.getTotalCount())
				.inboundCount(inboundCount)
				.usedCount(materialState.getUsedCount())
				.build();
		}
	}
}
