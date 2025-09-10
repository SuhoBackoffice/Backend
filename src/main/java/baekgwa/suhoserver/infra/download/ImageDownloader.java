package baekgwa.suhoserver.infra.download;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImageDownloader {

	/**
	 * 주어진 URL 문자열로부터 이미지를 다운로드하여 byte 배열로 반환합니다.
	 * 다운로드 실패 시 null을 반환합니다.
	 *
	 * @param imageUrl 다운로드할 이미지의 URL 문자열
	 * @return 성공 시 이미지 데이터의 byte 배열, 실패 시 null
	 */
	public static byte[] downloadImage(String imageUrl) {
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
			return null;
		}
	}
}
