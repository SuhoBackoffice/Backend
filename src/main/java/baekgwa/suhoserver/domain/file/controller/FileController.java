package baekgwa.suhoserver.domain.file.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import baekgwa.suhoserver.domain.file.service.FileService;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import baekgwa.suhoserver.infra.upload.FileType;
import baekgwa.suhoserver.infra.upload.FileUploadResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.file.controller
 * FileName    : FileController
 * Author      : Baekgwa
 * Date        : 2025-09-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-10     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Tag(name = "File Controller", description = "파일 업로드/삭제 컨트롤러")
public class FileController {

	private final FileService fileService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BaseResponse<FileUploadResponse> fileUpload(
		@RequestParam("file") MultipartFile file,
		@RequestParam("type") FileType type
	) {
		FileUploadResponse response = fileService.upload(file, type);
		return BaseResponse.success(SuccessCode.UPLOAD_FILE_SUCCESS, response);
	}

	@DeleteMapping
	public BaseResponse<Void> fileDelete(
		@RequestParam("fileUrl") String fileUrl
	) {
		fileService.delete(fileUrl);
		return BaseResponse.success(SuccessCode.DELETE_FILE_SUCCESS);
	}
}
