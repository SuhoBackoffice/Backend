package baekgwa.suhoserver.domain.version.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.version.dto
 * FileName    : VersionRequest
 * Author      : Baekgwa
 * Date        : 2025-08-05
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-05     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionRequest {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class NewVersionDto {
		private String versionName;
	}
}
