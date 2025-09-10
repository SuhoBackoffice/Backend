package baekgwa.suhoserver.infra.upload;

import lombok.Getter;

/**
 * PackageName : baekgwa.suhoserver.infra.upload
 * FileName    : FileUploadResponse
 * Author      : Baekgwa
 * Date        : 2025-09-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-10     Baekgwa               Initial creation
 */
@Getter
public abstract class FileUploadResponse {
	private final String fileName;
	private final String fileUrl;

	protected FileUploadResponse(String fileName, String fileUrl) {
		this.fileName = fileName;
		this.fileUrl = fileUrl;
	}
}
