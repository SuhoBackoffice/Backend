package baekgwa.suhoserver.domain.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import baekgwa.suhoserver.domain.project.type.ProjectSort;
import baekgwa.suhoserver.model.project.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.straight.entity.ProjectStraightEntity;
import lombok.AccessLevel;
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
		private final String branchCode;
		private final LocalDate branchVersion;
		private final Long totalQuantity;
		private final Long completedQuantity;
		private final Long branchTypeId;
		private final String branchName;
		private final String imageUrl;

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectBranchInfo(Long projectBranchId, String branchCode, LocalDate branchVersion, Long totalQuantity,
			Long completedQuantity, Long branchTypeId, String branchName, String imageUrl) {
			this.projectBranchId = projectBranchId;
			this.branchCode = branchCode;
			this.branchVersion = branchVersion;
			this.totalQuantity = totalQuantity;
			this.completedQuantity = completedQuantity;
			this.branchTypeId = branchTypeId;
			this.branchName = branchName;
			this.imageUrl = imageUrl;
		}

		public static ProjectBranchInfo of(ProjectBranchEntity projectBranch) {
			return ProjectBranchInfo
				.builder()
				.projectBranchId(projectBranch.getId())
				.branchCode(projectBranch.getBranchType().getCode())
				.branchVersion(projectBranch.getBranchType().getVersion())
				.totalQuantity(projectBranch.getTotalQuantity())
				.completedQuantity(projectBranch.getCompletedQuantity())
				.branchTypeId(projectBranch.getBranchType().getId())
				.branchName(projectBranch.getBranchType().getName())
				.imageUrl(projectBranch.getBranchType().getImageUrl())
				.build();
		}
	}

	@Getter
	public static class ProjectStraightInfo {
		private final Long straightRailId;
		private final Long length;
		private final Boolean isLoopRail;
		private final String straightType;
		private final Long totalQuantity;
		private final LitzInfo litzInfo;
		private final BigDecimal holePosition; //가공위치

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectStraightInfo(Long straightRailId, Long length, Boolean isLoopRail, String straightType,
			Long totalQuantity,
			LitzInfo litzInfo, BigDecimal holePosition) {
			this.straightRailId = straightRailId;
			this.length = length;
			this.isLoopRail = isLoopRail;
			this.straightType = straightType;
			this.totalQuantity = totalQuantity;
			this.litzInfo = litzInfo;
			this.holePosition = holePosition;
		}

		public static ProjectStraightInfo of(ProjectStraightEntity projectStraight, LitzInfo litzInfo,
			BigDecimal holePosition) {
			return ProjectStraightInfo
				.builder()
				.straightRailId(projectStraight.getId())
				.length(projectStraight.getLength())
				.isLoopRail(projectStraight.getIsLoopRail())
				.straightType(projectStraight.getStraightType().getType())
				.totalQuantity(projectStraight.getTotalQuantity())
				.litzInfo(litzInfo)
				.holePosition(holePosition)
				.build();
		}
	}

	@Getter
	public static class LitzInfo {
		private final BigDecimal litz1;
		private final BigDecimal litz2;
		private final BigDecimal litz3;
		private final BigDecimal litz4;
		private final BigDecimal litz5;
		private final BigDecimal litz6;

		@Builder(access = AccessLevel.PRIVATE)
		private LitzInfo(BigDecimal litz1, BigDecimal litz2, BigDecimal litz3, BigDecimal litz4, BigDecimal litz5,
			BigDecimal litz6) {
			this.litz1 = litz1;
			this.litz2 = litz2;
			this.litz3 = litz3;
			this.litz4 = litz4;
			this.litz5 = litz5;
			this.litz6 = litz6;
		}

		public static LitzInfo from(Map<Integer, BigDecimal> baseLitzWireMap) {
			return LitzInfo
				.builder()
				.litz1(baseLitzWireMap.getOrDefault(1, BigDecimal.ZERO))
				.litz2(baseLitzWireMap.getOrDefault(2, BigDecimal.ZERO))
				.litz3(baseLitzWireMap.getOrDefault(3, BigDecimal.ZERO))
				.litz4(baseLitzWireMap.getOrDefault(4, BigDecimal.ZERO))
				.litz5(baseLitzWireMap.getOrDefault(5, BigDecimal.ZERO))
				.litz6(baseLitzWireMap.getOrDefault(6, BigDecimal.ZERO))
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
}
