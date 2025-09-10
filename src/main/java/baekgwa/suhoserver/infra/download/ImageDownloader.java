package baekgwa.suhoserver.infra.download;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.infra.image.download
 * FileName    : ImageDownloader
 * Author      : Baekgwa
 * Date        : 2025-09-03
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-03     Baekgwa               Initial creation
 */
@Slf4j
@Component
public class ImageDownloader {

	private static final String DEFAULT_IMAGE_PATH = "static/images/no_image.png";
	private final byte[] defaultImageBytes;

	public ImageDownloader() {
		try (InputStream is = new ClassPathResource(DEFAULT_IMAGE_PATH).getInputStream()) {
			this.defaultImageBytes = is.readAllBytes();
		} catch (IOException e) {
			log.error("기본 이미지 로드 실패. static 이미지 확인해주세요. {}", e);
			throw new IllegalStateException("기본 이미지(no_image.png)를 로드할 수 없습니다.", e);
		}
	}

	/**
	 * 주어진 URL 문자열로부터 이미지를 다운로드하여 byte 배열로 반환합니다.
	 * - null 이면 기본 이미지 반환
	 * - 다운로드 실패 시 기본 이미지 반환
	 *
	 * @param imageUrl 다운로드할 이미지의 URL 문자열
	 * @return 성공 시 이미지 데이터의 byte 배열, 실패 시 기본 이미지
	 */
	public byte[] downloadImage(String imageUrl) {
		if (imageUrl == null || imageUrl.isBlank()) {
			return defaultImageBytes;
		}

		try {
			URL url = URI.create(imageUrl).toURL();

			try (InputStream in = url.openStream();
				 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

				byte[] buffer = new byte[1024 * 4];
				int n;

				while ((n = in.read(buffer)) != -1) {
					out.write(buffer, 0, n);
				}

				return out.toByteArray();
			}
		} catch (IOException e) {
			log.warn("Bom List 이미지 로드 실패. imageUrl = {}", imageUrl);
			return defaultImageBytes;
		}
	}
}
