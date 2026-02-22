package baekgwa.suhoserver.domain.material.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
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
		private final Long id;
		private final String drawingNumber;
		private final String itemName;

		@Builder(access = AccessLevel.PRIVATE)
		private MaterialInfo(String drawingNumber, String itemName, Long id) {
			this.id = id;
			this.drawingNumber = drawingNumber;
			this.itemName = itemName;
		}

		public static MaterialInfo of(BranchBomEntity branchBomEntity) {
			return MaterialInfo
				.builder()
				.id(branchBomEntity.getId())
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
		private MaterialHistoryDetail(Long id, String drawingNumber, String itemName, LocalDateTime receivedAt,
			Long quantity) {
			this.id = id;
			this.drawingNumber = drawingNumber;
			this.itemName = itemName;
			this.receivedAt = receivedAt;
			this.quantity = quantity;
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
