package baekgwa.suhoserver.domain.material.dto;

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
		private final Long count;

		@Builder(access = AccessLevel.PRIVATE)
		private MaterialHistory(LocalDate date, Long count) {
			this.date = date;
			this.count = count;
		}

		public static MaterialHistory of(LocalDate date, Long count) {
			return MaterialHistory.builder().date(date).count(count).build();
		}
	}

	@Getter
	public static class MaterialHistoryDetail {
		private final Long id;
		private final String drawingNumber;
		private final String itemName;
		private final LocalDateTime receivedAt;

		@Builder(access = AccessLevel.PRIVATE)
		private MaterialHistoryDetail(Long id, String drawingNumber, String itemName, LocalDateTime receivedAt) {
			this.id = id;
			this.drawingNumber = drawingNumber;
			this.itemName = itemName;
			this.receivedAt = receivedAt;
		}

		public static MaterialHistoryDetail of(MaterialInboundEntity materialInbound) {
			return MaterialHistoryDetail
				.builder()
				.id(materialInbound.getId())
				.drawingNumber(materialInbound.getDrawingNumber())
				.itemName(materialInbound.getItemName())
				.receivedAt(materialInbound.getCreatedAt())
				.build();
		}
	}
}
