package baekgwa.suhoserver.domain.project.dto;

import java.time.LocalDate;

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
		@NotNull(message = "레일 타입은 필수 입니다.")
		private Long straightTypeId;
		@Min(value = 1L, message = "생산 수량은 최소 1개 입니다.")
		private Long totalQuantity;
	}
}
