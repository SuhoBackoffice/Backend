package baekgwa.suhoserver.domain.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.material.project.entity.ProjectMaterialStockEntity;

import baekgwa.suhoserver.domain.project.type.ProjectBranchAnalyzeSort;
import baekgwa.suhoserver.domain.project.type.ProjectBranchCapacitySort;
import baekgwa.suhoserver.domain.project.type.ProjectSort;
import baekgwa.suhoserver.domain.project.type.ProjectStraightAnalyzeSort;
import baekgwa.suhoserver.domain.project.type.ProjectStraightCapacitySort;
import baekgwa.suhoserver.model.project.straight.bom.entity.ProjectStraightBomEntity;
import baekgwa.suhoserver.global.factory.ProductSerialFactory;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.serial.entity.ProjectBranchSerialEntity;
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
	public static class ProjectBranchDetailInfo {
		private final String serial; //generated serial
		private final Long totalQuantity;
		private final Long completedQuantity;
		private final String code;
		private final String name;
		private final LocalDate branchVersion;
		private final String imageUrl;
		private final List<BranchSerialInfo> serialInfoList;

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectBranchDetailInfo(String serial, Long totalQuantity, Long completedQuantity, String code,
			String name,
			LocalDate branchVersion, String imageUrl, List<BranchSerialInfo> serialInfoList) {
			this.serial = serial;
			this.totalQuantity = totalQuantity;
			this.completedQuantity = completedQuantity;
			this.code = code;
			this.name = name;
			this.branchVersion = branchVersion;
			this.imageUrl = imageUrl;
			this.serialInfoList = serialInfoList;
		}

		public static ProjectBranchDetailInfo of(ProjectBranchEntity branch, List<BranchSerialInfo> serialInfoList) {
			String branchSerial = ProductSerialFactory.generateBranchSerial(branch.getBranchType().getCode());

			return ProjectBranchDetailInfo
				.builder()
				.serial(branchSerial)
				.totalQuantity(branch.getTotalQuantity())
				.completedQuantity(branch.getCompletedQuantity())
				.code(branch.getBranchType().getName())
				.name(branch.getBranchType().getName())
				.branchVersion(branch.getBranchType().getVersion())
				.imageUrl(branch.getBranchType().getImageUrl())
				.serialInfoList(serialInfoList)
				.build();
		}
	}

	@Getter
	public static class BranchSerialInfo {
		private final String serial;
		private final String serialState;
		private final String productionState;
		private final LocalDate producedAt;
		private final String inactiveReason;

		@Builder(access = AccessLevel.PRIVATE)
		private BranchSerialInfo(String serial, String serialState, String productionState, LocalDate producedAt,
			String inactiveReason) {
			this.serial = serial;
			this.serialState = serialState;
			this.productionState = productionState;
			this.producedAt = producedAt;
			this.inactiveReason = inactiveReason;
		}

		public static BranchSerialInfo of(ProjectBranchSerialEntity serial) {
			return BranchSerialInfo
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
		private final Long projectBranchId;
		private final String serial;
		private final String name;
		private final Long totalQuantity;
		private final Long completedQuantity;
		private final Long capacity; // 생산 가능 수량(대수) - 입고된 자재 기준으로 물리적으로 제작 가능한 총 수량
		private final Long remainingQuantity; // totalQuantity - completedQuantity
		private final Long effectiveCapacity; // min(capacity, remainingQuantity)

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectBranchCapacity(String imageUrl, Long projectBranchId, String serial, String name,
			Long totalQuantity, Long completedQuantity, Long capacity, Long remainingQuantity, Long effectiveCapacity) {
			this.imageUrl = imageUrl;
			this.projectBranchId = projectBranchId;
			this.serial = serial;
			this.name = name;
			this.totalQuantity = totalQuantity;
			this.completedQuantity = completedQuantity;
			this.capacity = capacity;
			this.remainingQuantity = remainingQuantity;
			this.effectiveCapacity = effectiveCapacity;
		}

		public static ProjectBranchCapacity of(ProjectBranchEntity pb, long capacity) {

			String branchSerial = ProductSerialFactory.generateBranchSerial(pb.getBranchType().getCode());

			long remainingQuantity = pb.getTotalQuantity() - pb.getCompletedQuantity();
			long effectiveCapacity = Math.min(capacity, remainingQuantity);

			return ProjectBranchCapacity.builder()
				.imageUrl(pb.getBranchType().getImageUrl())
				.projectBranchId(pb.getId())
				.serial(branchSerial)
				.name(pb.getBranchType().getName())
				.totalQuantity(pb.getTotalQuantity())
				.completedQuantity(pb.getCompletedQuantity())
				.capacity(capacity)
				.remainingQuantity(remainingQuantity)
				.effectiveCapacity(effectiveCapacity)
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

	@Getter
	public static class BranchCapacitySortType {
		private final String sort;
		private final String description;

		private BranchCapacitySortType(String sort, String description) {
			this.sort = sort;
			this.description = description;
		}

		public static BranchCapacitySortType from(ProjectBranchCapacitySort sortType) {
			return new BranchCapacitySortType(sortType.name(), sortType.getDescription());
		}

		public static BranchCapacitySortType from(ProjectBranchAnalyzeSort sortType) {
			return new BranchCapacitySortType(sortType.name(), sortType.getDescription());
		}

		public static BranchCapacitySortType from(ProjectStraightCapacitySort sortType) {
			return new BranchCapacitySortType(sortType.name(), sortType.getDescription());
		}

		public static BranchCapacitySortType from(ProjectStraightAnalyzeSort sortType) {
			return new BranchCapacitySortType(sortType.name(), sortType.getDescription());
		}
	}

	@Getter
	public static class BomShortageInfo {
		private final String drawingNumber;
		private final String itemName;
		private final String itemType;
		private final String specification;
		private final Long unitQuantity;
		private final String unit;
		private final Boolean suppliedMaterial;
		private final Long stockQuantity;
		private final Long requiredQuantity;
		private final Long shortageQuantity;
		private final Boolean isShortage;
		private final Long availableCapacity;

		@Builder(access = AccessLevel.PRIVATE)
		private BomShortageInfo(String drawingNumber, String itemName, String itemType, String specification,
			Long unitQuantity, String unit, Boolean suppliedMaterial, Long stockQuantity, Long requiredQuantity,
			Long shortageQuantity, Boolean isShortage, Long availableCapacity) {
			this.drawingNumber = drawingNumber;
			this.itemName = itemName;
			this.itemType = itemType;
			this.specification = specification;
			this.unitQuantity = unitQuantity;
			this.unit = unit;
			this.suppliedMaterial = suppliedMaterial;
			this.stockQuantity = stockQuantity;
			this.requiredQuantity = requiredQuantity;
			this.shortageQuantity = shortageQuantity;
			this.isShortage = isShortage;
			this.availableCapacity = availableCapacity;
		}

		public static BomShortageInfo of(BranchBomEntity bom, ProjectMaterialStockEntity stock, long remainingBranchQty) {
			long inboundQty = stock != null ? stock.getTotalInboundQuantity() : 0L;
			long usedQty = stock != null ? stock.getTotalUsedQuantity() : 0L;
			long stockQty = inboundQty - usedQty;
			long requiredQty = bom.getUnitQuantity() * remainingBranchQty;
			long shortageQty = Math.max(0L, requiredQty - stockQty);
			long availableCap = (stockQty > 0 && bom.getUnitQuantity() > 0)
				? stockQty / bom.getUnitQuantity() : 0L;

			return BomShortageInfo.builder()
				.drawingNumber(bom.getDrawingNumber())
				.itemName(bom.getItemName())
				.itemType(bom.getItemType())
				.specification(bom.getSpecification())
				.unitQuantity(bom.getUnitQuantity())
				.unit(bom.getUnit())
				.suppliedMaterial(bom.getSuppliedMaterial())
				.stockQuantity(stockQty)
				.requiredQuantity(requiredQty)
				.shortageQuantity(shortageQty)
				.isShortage(shortageQty > 0)
				.availableCapacity(availableCap)
				.build();
		}
	}

	@Getter
	public static class ProjectBranchCapacityAnalyze {
		private final String serial;
		private final String code;
		private final String name;
		private final Long totalQuantity;
		private final Long completedQuantity;
		private final Long remainingQuantity;
		private final Long capacity;
		private final Long effectiveCapacity;
		private final List<BomShortageInfo> bomShortageList;

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectBranchCapacityAnalyze(String serial, String code, String name, Long totalQuantity,
			Long completedQuantity, Long remainingQuantity, Long capacity, Long effectiveCapacity,
			List<BomShortageInfo> bomShortageList) {
			this.serial = serial;
			this.code = code;
			this.name = name;
			this.totalQuantity = totalQuantity;
			this.completedQuantity = completedQuantity;
			this.remainingQuantity = remainingQuantity;
			this.capacity = capacity;
			this.effectiveCapacity = effectiveCapacity;
			this.bomShortageList = bomShortageList;
		}

		public static ProjectBranchCapacityAnalyze of(ProjectBranchEntity pb, long capacity,
			List<BomShortageInfo> bomShortageList) {
			String branchSerial = baekgwa.suhoserver.global.factory.ProductSerialFactory
				.generateBranchSerial(pb.getBranchType().getCode());
			long remainingQuantity = pb.getTotalQuantity() - pb.getCompletedQuantity();
			long effectiveCapacity = Math.min(capacity, remainingQuantity);

			return ProjectBranchCapacityAnalyze.builder()
				.serial(branchSerial)
				.code(pb.getBranchType().getCode())
				.name(pb.getBranchType().getName())
				.totalQuantity(pb.getTotalQuantity())
				.completedQuantity(pb.getCompletedQuantity())
				.remainingQuantity(remainingQuantity)
				.capacity(capacity)
				.effectiveCapacity(effectiveCapacity)
				.bomShortageList(bomShortageList)
				.build();
		}
	}

	@Getter
	public static class StraightBomShortageInfo {
		private final String materialCode;
		private final String itemName;
		private final Long unitQuantity;
		private final Long stockQuantity;
		private final Long requiredQuantity;
		private final Long shortageQuantity;
		private final Boolean isShortage;
		private final Long availableCapacity;

		@Builder(access = AccessLevel.PRIVATE)
		private StraightBomShortageInfo(String materialCode, String itemName, Long unitQuantity,
			Long stockQuantity, Long requiredQuantity, Long shortageQuantity, Boolean isShortage,
			Long availableCapacity) {
			this.materialCode = materialCode;
			this.itemName = itemName;
			this.unitQuantity = unitQuantity;
			this.stockQuantity = stockQuantity;
			this.requiredQuantity = requiredQuantity;
			this.shortageQuantity = shortageQuantity;
			this.isShortage = isShortage;
			this.availableCapacity = availableCapacity;
		}

		public static StraightBomShortageInfo of(
			ProjectStraightBomEntity bom,
			baekgwa.suhoserver.model.material.project.entity.ProjectMaterialStockEntity stock,
			long remainingQty
		) {
			long inboundQty = stock != null ? stock.getTotalInboundQuantity() : 0L;
			long usedQty = stock != null ? stock.getTotalUsedQuantity() : 0L;
			long stockQty = inboundQty - usedQty;
			long requiredQty = bom.getUnitQuantity() * remainingQty;
			long shortageQty = Math.max(0L, requiredQty - stockQty);
			long availableCap = (stockQty > 0 && bom.getUnitQuantity() > 0)
				? stockQty / bom.getUnitQuantity() : 0L;

			return StraightBomShortageInfo.builder()
				.materialCode(bom.getMaterialCode())
				.itemName(bom.getItemName())
				.unitQuantity(bom.getUnitQuantity())
				.stockQuantity(stockQty)
				.requiredQuantity(requiredQty)
				.shortageQuantity(shortageQty)
				.isShortage(shortageQty > 0)
				.availableCapacity(availableCap)
				.build();
		}
	}

	@Getter
	public static class ProjectStraightCapacity {
		private final Long projectStraightId;
		private final String serial;
		private final Long length;
		private final Boolean isLoopRail;
		private final Long totalQuantity;
		private final Long completedQuantity;
		private final Long capacity;
		private final Long remainingQuantity;
		private final Long effectiveCapacity;

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectStraightCapacity(Long projectStraightId, String serial, Long length, Boolean isLoopRail,
			Long totalQuantity, Long completedQuantity, Long capacity, Long remainingQuantity,
			Long effectiveCapacity) {
			this.projectStraightId = projectStraightId;
			this.serial = serial;
			this.length = length;
			this.isLoopRail = isLoopRail;
			this.totalQuantity = totalQuantity;
			this.completedQuantity = completedQuantity;
			this.capacity = capacity;
			this.remainingQuantity = remainingQuantity;
			this.effectiveCapacity = effectiveCapacity;
		}

		public static ProjectStraightCapacity of(ProjectStraightEntity ps, long capacity) {
			String straightSerial = ProductSerialFactory.generateStraightSerial(
				ps.getLength(), ps.getIsLoopRail(), ps.getStraightType().getType());
			long remainingQuantity = ps.getTotalQuantity() - ps.getCompletedQuantity();
			long effectiveCapacity = Math.min(capacity, remainingQuantity);

			return ProjectStraightCapacity.builder()
				.projectStraightId(ps.getId())
				.serial(straightSerial)
				.length(ps.getLength())
				.isLoopRail(ps.getIsLoopRail())
				.totalQuantity(ps.getTotalQuantity())
				.completedQuantity(ps.getCompletedQuantity())
				.capacity(capacity)
				.remainingQuantity(remainingQuantity)
				.effectiveCapacity(effectiveCapacity)
				.build();
		}
	}

	@Getter
	public static class ProjectStraightCapacityAnalyze {
		private final String serial;
		private final Long length;
		private final Boolean isLoopRail;
		private final Long totalQuantity;
		private final Long completedQuantity;
		private final Long remainingQuantity;
		private final Long capacity;
		private final Long effectiveCapacity;
		private final List<StraightBomShortageInfo> bomShortageList;

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectStraightCapacityAnalyze(String serial, Long length, Boolean isLoopRail,
			Long totalQuantity, Long completedQuantity, Long remainingQuantity, Long capacity,
			Long effectiveCapacity, List<StraightBomShortageInfo> bomShortageList) {
			this.serial = serial;
			this.length = length;
			this.isLoopRail = isLoopRail;
			this.totalQuantity = totalQuantity;
			this.completedQuantity = completedQuantity;
			this.remainingQuantity = remainingQuantity;
			this.capacity = capacity;
			this.effectiveCapacity = effectiveCapacity;
			this.bomShortageList = bomShortageList;
		}

		public static ProjectStraightCapacityAnalyze of(ProjectStraightEntity ps, long capacity,
			List<StraightBomShortageInfo> bomShortageList) {
			String straightSerial = ProductSerialFactory.generateStraightSerial(
				ps.getLength(), ps.getIsLoopRail(), ps.getStraightType().getType());
			long remainingQuantity = ps.getTotalQuantity() - ps.getCompletedQuantity();
			long effectiveCapacity = Math.min(capacity, remainingQuantity);

			return ProjectStraightCapacityAnalyze.builder()
				.serial(straightSerial)
				.length(ps.getLength())
				.isLoopRail(ps.getIsLoopRail())
				.totalQuantity(ps.getTotalQuantity())
				.completedQuantity(ps.getCompletedQuantity())
				.remainingQuantity(remainingQuantity)
				.capacity(capacity)
				.effectiveCapacity(effectiveCapacity)
				.bomShortageList(bomShortageList)
				.build();
		}
	}
}
