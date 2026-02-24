package baekgwa.suhoserver.domain.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import baekgwa.suhoserver.domain.project.type.ProjectSort;
import baekgwa.suhoserver.global.factory.ProductSerialFactory;
import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.serial.entity.ProjectStraightSerialEntity;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.dto
 * FileName    : ProjectResponse
 * Author      : Baekgwa
 * Date        : 2025-08-07
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-07     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectResponse {

	@Getter
	public static class NewProjectDto {
		private final Long projectId;

		public NewProjectDto(Long projectId) {
			this.projectId = projectId;
		}
	}

	@Getter
	public static class ProjectDetailInfo {
		private final Long versionInfoId;
		private final String version;
		private final String region;
		private final String name;
		private final LocalDate startDate;
		private final LocalDate endDate;

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectDetailInfo(Long versionInfoId, String version, String region, String name, LocalDate startDate,
			LocalDate endDate) {
			this.versionInfoId = versionInfoId;
			this.version = version;
			this.region = region;
			this.name = name;
			this.startDate = startDate;
			this.endDate = endDate;
		}

		public static ProjectDetailInfo of(ProjectEntity project) {
			return ProjectDetailInfo
				.builder()
				.versionInfoId(project.getVersionInfoEntity().getId())
				.version(project.getVersionInfoEntity().getName())
				.region(project.getRegion())
				.name(project.getName())
				.startDate(project.getStartDate())
				.endDate(project.getEndDate())
				.build();
		}
	}

	@Getter
	public static class ProjectBranchInfo {
		private final Long projectBranchId;
		private final String branchName;
		private final String branchSerial;
		private final Long totalQuantity;
		private final Long completedQuantity;

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectBranchInfo(Long projectBranchId, String branchName, String branchSerial,
			Long totalQuantity, Long completedQuantity) {
			this.projectBranchId = projectBranchId;
			this.branchName = branchName;
			this.branchSerial = branchSerial;
			this.totalQuantity = totalQuantity;
			this.completedQuantity = completedQuantity;
		}

		public static ProjectBranchInfo of(ProjectBranchEntity branch) {
			String serial = ProductSerialFactory.generateBranchSerial(branch.getBranchType().getCode());
			return ProjectBranchInfo
				.builder()
				.projectBranchId(branch.getId())
				.branchName(branch.getBranchType().getName())
				.branchSerial(serial)
				.totalQuantity(branch.getTotalQuantity())
				.completedQuantity(branch.getCompletedQuantity())
				.build();
		}
	}

	@Getter
	public static class ProjectStraightDetailInfo {
		private final String serial;
		private final Long length;
		private final Long totalQuantity;
		private final Long completedQuantity;
		private final Boolean isLoopRail;
		private final BigDecimal holePosition;
		private final List<StraightSerialInfo> serialInfoList;

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectStraightDetailInfo(String serial, Long length, Long totalQuantity, Long completedQuantity,
			Boolean isLoopRail, BigDecimal holePosition, List<StraightSerialInfo> serialInfoList) {
			this.serial = serial;
			this.length = length;
			this.totalQuantity = totalQuantity;
			this.completedQuantity = completedQuantity;
			this.isLoopRail = isLoopRail;
			this.holePosition = holePosition;
			this.serialInfoList = serialInfoList;
		}

		public static ProjectStraightDetailInfo of(ProjectStraightEntity straight, List<StraightSerialInfo> serialInfoList) {
			String straightSerial = ProductSerialFactory.generateStraightSerial(straight.getLength(), straight.getIsLoopRail(),
				straight.getStraightType().getType());

			return ProjectStraightDetailInfo
				.builder()
				.serial(straightSerial)
				.length(straight.getLength())
				.totalQuantity(straight.getTotalQuantity())
				.completedQuantity(straight.getCompletedQuantity())
				.isLoopRail(straight.getIsLoopRail())
				.holePosition(straight.getHolePosition())
				.serialInfoList(serialInfoList)
				.build();
		}
	}

	@Getter
	public static class StraightSerialInfo {
		private final String serial;
		private final String serialState;
		private final String productionState;
		private final LocalDate producedAt;
		private final String inactiveReason;

		@Builder(access = AccessLevel.PRIVATE)
		private StraightSerialInfo(String serial, String serialState, String productionState, LocalDate producedAt,
			String inactiveReason) {
			this.serial = serial;
			this.serialState = serialState;
			this.productionState = productionState;
			this.producedAt = producedAt;
			this.inactiveReason = inactiveReason;
		}

		public static StraightSerialInfo of(ProjectStraightSerialEntity serial) {
			return StraightSerialInfo
				.builder()
				.serial(serial.getSerial())
				.serialState(serial.getState().getDescription())
				.productionState(serial.getProductionState().getDescription())
				.producedAt(serial.getProducedAt())
				.inactiveReason(
					serial.getReason() != null
						? serial.getReason().getDescription()
						: null
				)
				.build();
		}
	}

	@Getter
	public static class ProjectStraightInfo {
		private final List<ProjectNormalStraightInfo> normalStraightList;
		private final List<ProjectLoopStraightInfo> loopStraightList;

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectStraightInfo(
			List<ProjectNormalStraightInfo> normalStraightList,
			List<ProjectLoopStraightInfo> loopStraightList
		) {
			this.normalStraightList = normalStraightList;
			this.loopStraightList = loopStraightList;
		}

		public static ProjectStraightInfo of(List<ProjectNormalStraightInfo> normal, List<ProjectLoopStraightInfo> loop) {
			return ProjectStraightInfo
				.builder()
				.normalStraightList(normal)
				.loopStraightList(loop)
				.build();
		}
	}

	@Getter
	public static class ProjectNormalStraightInfo {
		private final Long straightRailId;
		private final String serial;
		private final Long totalQuantity;
		private final Long completedQuantity;

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectNormalStraightInfo(Long totalQuantity, Long straightRailId, String serial,
			Long completedQuantity) {
			this.totalQuantity = totalQuantity;
			this.straightRailId = straightRailId;
			this.serial = serial;
			this.completedQuantity = completedQuantity;
		}

		public static ProjectNormalStraightInfo of(ProjectStraightEntity straight) {
			String straightSerial = ProductSerialFactory.generateStraightSerial(straight.getLength(), straight.getIsLoopRail(),
				straight.getStraightType().getType());

			return ProjectNormalStraightInfo
				.builder()
				.straightRailId(straight.getId())
				.serial(straightSerial)
				.totalQuantity(straight.getTotalQuantity())
				.completedQuantity(straight.getCompletedQuantity())
				.build();
		}
	}

	@Getter
	public static class ProjectLoopStraightInfo {
		private final Long straightRailId;
		private final String serial;
		private final Long totalQuantity;
		private final Long completedQuantity;
		private final BigDecimal holePosition;

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectLoopStraightInfo(Long straightRailId, String serial, Long totalQuantity, Long completedQuantity,
			BigDecimal holePosition) {
			this.straightRailId = straightRailId;
			this.serial = serial;
			this.totalQuantity = totalQuantity;
			this.completedQuantity = completedQuantity;
			this.holePosition = holePosition;
		}

		public static ProjectLoopStraightInfo of(ProjectStraightEntity straight) {
			String straightSerial = ProductSerialFactory.generateStraightSerial(straight.getLength(), straight.getIsLoopRail(),
				straight.getStraightType().getType());

			return ProjectLoopStraightInfo
				.builder()
				.straightRailId(straight.getId())
				.serial(straightSerial)
				.totalQuantity(straight.getTotalQuantity())
				.completedQuantity(straight.getCompletedQuantity())
				.holePosition(straight.getHolePosition())
				.build();
		}
	}

	@Getter
	public static class ProjectInfo {
		private final Long id;
		private final String version;
		private final String region;
		private final String name;
		private final LocalDate startDate;
		private final LocalDate endDate;

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectInfo(Long id, String version, String region, String name, LocalDate startDate,
			LocalDate endDate) {
			this.id = id;
			this.version = version;
			this.region = region;
			this.name = name;
			this.startDate = startDate;
			this.endDate = endDate;
		}

		public static ProjectInfo of(ProjectEntity project) {
			return ProjectInfo
				.builder()
				.id(project.getId())
				.version(project.getVersionInfoEntity().getName())
				.region(project.getRegion())
				.name(project.getName())
				.startDate(project.getStartDate())
				.endDate(project.getEndDate())
				.build();
		}
	}

	@Getter
	public static class ProjectSearchSort {
		private final String id;
		private final String name;

		private ProjectSearchSort(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public static ProjectSearchSort of(ProjectSort sort) {
			return new ProjectSearchSort(sort.name(), sort.getDescription());
		}
	}

	@Getter
	public static class ProjectQuantityList {
		private final byte[] excelBytes;
		private final String fileName; //인코딩 후, 넣을 것

		public ProjectQuantityList(byte[] excelBytes, String fileName) {
			this.excelBytes = excelBytes;
			this.fileName = fileName;
		}
	}

	@Getter
	public static class ProjectBranchCapacity {
		private final String imageUrl;
		private final Long branchTypeId;
		private final String code;
		private final String name;
		private final Long totalQuantity;
		private final Long completedQuantity;
		private final Long capacity;
		private final List<BranchBomShortage> branchBomShortageList;

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectBranchCapacity(String imageUrl, Long branchTypeId, String code, String name, Long totalQuantity,
			Long completedQuantity, Long capacity, List<BranchBomShortage> branchBomShortageList
		) {
			this.imageUrl = imageUrl;
			this.branchTypeId = branchTypeId;
			this.code = code;
			this.name = name;
			this.totalQuantity = totalQuantity;
			this.completedQuantity = completedQuantity;
			this.capacity = capacity;
			this.branchBomShortageList = branchBomShortageList;
		}

		public static ProjectBranchCapacity of(ProjectBranchEntity pb, long capacity, List<BranchBomShortage> branchBomShortageList) {
			return ProjectBranchCapacity.builder()
				.imageUrl(pb.getBranchType().getImageUrl())
				.branchTypeId(pb.getBranchType().getId())
				.code(pb.getBranchType().getCode())
				.name(pb.getBranchType().getName())
				.totalQuantity(pb.getTotalQuantity())
				.completedQuantity(pb.getCompletedQuantity())
				.capacity(capacity)
				.branchBomShortageList(branchBomShortageList)
				.build();
		}
	}

	@Getter
	public static class BranchBomShortage {
		private final String drawingNumber;
		private final String itemName;
		private final Long shortage; // 부족 수량

		@Builder(access = AccessLevel.PRIVATE)
		private BranchBomShortage(String drawingNumber, String itemName, Long shortage) {
			this.drawingNumber = drawingNumber;
			this.itemName = itemName;
			this.shortage = shortage;
		}

		public static BranchBomShortage of(BranchBomEntity bom, long available, long remainingTarget) {
			long unit = (bom.getUnitQuantity() == null ? 0L : bom.getUnitQuantity());
			if (unit <= 0L) return null;

			// 목표 세트 수량 달성에 필요한 총 자재량
			long requiredTotal = unit * remainingTarget;
			long shortage = Math.max(0L, requiredTotal - available);

			if (shortage == 0L) return null;

			return BranchBomShortage.builder()
				.drawingNumber(bom.getDrawingNumber())
				.itemName(bom.getItemName())
				.shortage(shortage)
				.build();
		}
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@Builder(access = AccessLevel.PRIVATE)
	public static class OnGoingProjectInfo {
		private final Long projectId;
		private final String version;
		private final String region;
		private final String name;
		private final LocalDate startDate;
		private final LocalDate endDate;

		public static OnGoingProjectInfo of(ProjectEntity project) {
			return OnGoingProjectInfo
				.builder()
				.projectId(project.getId())
				.version(project.getVersionInfoEntity().getName())
				.region(project.getRegion())
				.name(project.getName())
				.startDate(project.getStartDate())
				.endDate(project.getEndDate())
				.build();
		}
	}
}
