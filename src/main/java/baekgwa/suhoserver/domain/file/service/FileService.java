package baekgwa.suhoserver.domain.file.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import baekgwa.suhoserver.infra.upload.FileClient;
import baekgwa.suhoserver.infra.upload.FileType;
import baekgwa.suhoserver.infra.upload.FileUploadResponse;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.file.service
 * FileName    : FileService
 * Author      : Baekgwa
 * Date        : 2025-09-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-10     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class FileService {

	private final FileClient fileClient;

	@Transactional
	public FileUploadResponse upload(MultipartFile file, FileType type) {
		return fileClient.uploadFile(type, file);
	}

	@Transactional
	public void delete(String fileUrl) {
		fileClient.deleteFile(fileUrl);
	}
}
