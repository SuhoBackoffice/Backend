package baekgwa.suhoserver.infra.upload;

import org.springframework.web.multipart.MultipartFile;

/**
 * PackageName : baekgwa.suhoserver.infra.upload
 * FileName    : FileClient
 * Author      : Baekgwa
 * Date        : 2025-09-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-10     Baekgwa               Initial creation
 */
public interface FileClient {

	FileUploadResponse uploadFile(FileType type, MultipartFile file);
	void deleteFile(String fileUrl);
}
