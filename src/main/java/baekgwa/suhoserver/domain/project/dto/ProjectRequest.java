package baekgwa.suhoserver.domain.project.dto;

import java.time.LocalDate;

import baekgwa.suhoserver.domain.project.type.ProjectSort;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.dto
 * FileName    : ProjectRequest
 * Author      : Baekgwa
 * Date        : 2025-08-07
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-07     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectRequest {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class PostNewProjectDto {
		@NotNull(message = "프로젝트 버전은 필수 입니다.")
		private Long versionId;

		@NotNull(message = "프로젝트 납품 지역은 필수 입니다.")
		private String region;

		@NotNull(message = "프로젝트 명은 필수 입니다.")
		private String name;

		private LocalDate startDate;
		private LocalDate endDate;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class PostProjectBranchInfo {
		@NotNull(message = "분기 레일 정보는 필수 입니다.")
		private Long branchTypeId;
		@Min(value = 1L, message = "생산 수량은 최소 1개 입니다.")
		private Long quantity;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class PostProjectStraightInfo {
		@Min(value = 300L, message = "레일 길이는 최소 300 부터입니다.")
		private Long length;
		@NotNull(message = "레일 타입은 필수 입니다.")
		private Long straightTypeId;
		@Min(value = 1L, message = "생산 수량은 최소 1개 입니다.")
		private Long totalQuantity;
		private boolean isLoopRail;
	}

	@Getter
	public static class GetProjectInfo {
		final String keyword;
		final int page;
		final int size;
		final Long versionId;
		final LocalDate startDate;
		final LocalDate endDate;
		final ProjectSort sort;

		public GetProjectInfo(String keyword, int page, int size, Long versionId, LocalDate startDate,
			LocalDate endDate, ProjectSort sort) {
			this.keyword = keyword;
			this.page = page;
			this.size = size;
			this.versionId = versionId;
			this.startDate = startDate;
			this.endDate = endDate;
			this.sort = sort;
		}
	}
}
