package baekgwa.suhoserver.domain.version.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.version.dto
 * FileName    : VersionResponse
 * Author      : Baekgwa
 * Date        : 2025-08-05
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-05     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionResponse {

	@Getter
	public static class VersionListDto {
		private final Long id;
		private final String name;

		@Builder(access = AccessLevel.PRIVATE)
		private VersionListDto(Long id, String name) {
			this.id = id;
			this.name = name;
		}

		public static VersionListDto of(Long id, String name) {
			return VersionListDto
				.builder()
				.id(id)
				.name(name)
				.build();
		}
	}
}
