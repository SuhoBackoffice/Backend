package baekgwa.suhoserver.infra.upload.s3;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import baekgwa.suhoserver.global.environment.S3Properties;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.infra.upload.FileClient;
import baekgwa.suhoserver.infra.upload.FileType;
import baekgwa.suhoserver.infra.upload.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * PackageName : baekgwa.suhoserver.infra.upload.s3
 * FileName    : S3FileClient
 * Author      : Baekgwa
 * Date        : 2025-09-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-10     Baekgwa               Initial creation
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class S3FileClient implements FileClient {

	private final S3Client s3Client;
	private final S3Properties s3Properties;

	@Override
	public FileUploadResponse uploadFile(FileType type, MultipartFile file) {
		if (file.isEmpty()) {
			throw new GlobalException(ErrorCode.UPLOAD_FILE_FAIL);
		}

		String key = generateRandomFileKey(type.getPath());

		try {
			PutObjectRequest objectRequest = PutObjectRequest
				.builder()
				.bucket(s3Properties.getBucket())
				.key(key)
				.contentType(resolveContentType(file))
				.contentLength(file.getSize())
				.build();
			s3Client.putObject(objectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
		} catch (Exception e) {
			log.warn("S3 파일 업로드 실패 : {}", e.getMessage());
			throw new GlobalException(ErrorCode.UPLOAD_FILE_FAIL);
		}

		String saveGetUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", s3Properties.getBucket(),
			s3Properties.getRegion(), key);
		return new S3FileUploadResponse("image", saveGetUrl, s3Properties.getBucket());
	}

	@Override
	public void deleteFile(String fileUrl) {
		if (!StringUtils.hasText(fileUrl)) {
			throw new GlobalException(ErrorCode.DELETE_FILE_FAIL);
		}
		try {
			S3Ref ref = S3Ref.fromVirtualHostedUrl(
				fileUrl, s3Properties.getBucket(), s3Properties.getRegion()
			);

			s3Client.deleteObject(DeleteObjectRequest.builder()
				.bucket(ref.getBucket())
				.key(ref.getKey())
				.build());
		} catch (GlobalException e) {
			throw e;
		} catch (Exception e) {
			log.warn("S3 파일 삭제 실패: url={}, err={}", fileUrl, e.toString());
			throw new GlobalException(ErrorCode.DELETE_FILE_FAIL);
		}
	}

	/**
	 * ContentType 확인 및 fallback 처리
	 */
	private String resolveContentType(MultipartFile file) {
		String contentType = file.getContentType();
		if (!StringUtils.hasText(contentType)) {
			String name = file.getOriginalFilename().toLowerCase();
			if (!StringUtils.hasText(name)) {
				return MediaType.APPLICATION_OCTET_STREAM_VALUE;
			}

			String ext = getExtension(name);
			if (!StringUtils.hasText(ext)) {
				return MediaType.APPLICATION_OCTET_STREAM_VALUE;
			}

			return switch (ext) {
				case "mp4" -> MediaType.valueOf("video/mp4").toString();
				case "webm" -> MediaType.valueOf("video/webm").toString();
				case "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE;
				case "png" -> MediaType.IMAGE_PNG_VALUE;
				case "pdf" -> MediaType.APPLICATION_PDF_VALUE;
				default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
			};
		}
		return contentType;
	}

	private String getExtension(String filename) {
		int lastDot = filename.lastIndexOf('.');
		if (lastDot == -1 || lastDot == filename.length() - 1) {
			return null;
		}
		return filename.substring(lastDot + 1);
	}

	private String generateRandomFileKey(String path) {
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String uuid = UUID.randomUUID().toString().substring(0, 8);
		return path + "/" + time + "_" + uuid;
	}
}

