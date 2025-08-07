package baekgwa.suhoserver.domain.project.dto;

import lombok.AccessLevel;
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
}
