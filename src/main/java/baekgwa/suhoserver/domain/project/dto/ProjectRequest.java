package baekgwa.suhoserver.domain.project.dto;

import java.time.LocalDate;

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
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PostNewProjectDto {
		private Long versionId;
		private String region;
		private LocalDate startDate;
		private LocalDate endDate;
	}
}
