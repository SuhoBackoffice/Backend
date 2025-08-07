package baekgwa.suhoserver.domain.project.dto;

import java.time.LocalDate;
import java.util.List;

import baekgwa.suhoserver.model.project.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
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
	public static class ProjectInfo {
		private final String version;
		private final String region;
		private final String name;
		private final LocalDate startDate;
		private final LocalDate endDate;
		private final List<BranchInfo> branchInfoList;
		//todo : 직선 레일 정보

		@Builder(access = AccessLevel.PRIVATE)
		private ProjectInfo(String version, String region, String name, LocalDate startDate, LocalDate endDate,
			List<BranchInfo> branchInfoList) {
			this.version = version;
			this.region = region;
			this.name = name;
			this.startDate = startDate;
			this.endDate = endDate;
			this.branchInfoList = branchInfoList;
		}

		public static ProjectInfo of(ProjectEntity project, List<BranchInfo> branchInfoList) {
			return ProjectInfo
				.builder()
				.version(project.getVersionInfoEntity().getName())
				.region(project.getRegion())
				.name(project.getName())
				.startDate(project.getStartDate())
				.endDate(project.getEndDate())
				.branchInfoList(branchInfoList)
				.build();
		}
	}

	@Getter
	public static class BranchInfo {
		private final String branchCode;
		private final LocalDate branchVersion;
		private final Long totalQuantity;
		private final Long completedQuantity;

		@Builder(access = AccessLevel.PRIVATE)
		private BranchInfo(String branchCode, LocalDate branchVersion, Long totalQuantity, Long completedQuantity) {
			this.branchCode = branchCode;
			this.branchVersion = branchVersion;
			this.totalQuantity = totalQuantity;
			this.completedQuantity = completedQuantity;
		}

		public static BranchInfo of(ProjectBranchEntity projectBranch) {
			return BranchInfo
				.builder()
				.branchCode(projectBranch.getBranchType().getCode())
				.branchVersion(projectBranch.getBranchType().getVersion())
				.totalQuantity(projectBranch.getTotalQuantity())
				.completedQuantity(projectBranch.getCompletedQuantity())
				.build();
		}
	}
}
