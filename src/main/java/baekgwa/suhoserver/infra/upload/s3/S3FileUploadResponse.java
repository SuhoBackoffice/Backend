package baekgwa.suhoserver.infra.upload.s3;

import baekgwa.suhoserver.infra.upload.FileUploadResponse;
import lombok.Getter;

/**
 * PackageName : baekgwa.suhoserver.infra.upload.s3
 * FileName    : S3FileUploadResponse
 * Author      : Baekgwa
 * Date        : 2025-09-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-10     Baekgwa               Initial creation
 */
@Getter
public class S3FileUploadResponse extends FileUploadResponse {
	private final String bucket;

	public S3FileUploadResponse(String fileName, String fileUrl, String bucket) {
		super(fileName, fileUrl);
		this.bucket = bucket;
	}
}