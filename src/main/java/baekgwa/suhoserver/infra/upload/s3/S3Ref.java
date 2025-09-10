package baekgwa.suhoserver.infra.upload.s3;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.util.StringUtils;

import lombok.Getter;

/**
 * PackageName : baekgwa.suhoserver.infra.upload.s3
 * FileName    : S3Ref
 * Author      : Baekgwa
 * Date        : 2025-09-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-10     Baekgwa               Initial creation
 */
@Getter
public class S3Ref {
	private final String bucket;
	private final String key;

	private S3Ref(String bucket, String key) {
		this.bucket = bucket;
		this.key = key;
	}

	public static S3Ref fromVirtualHostedUrl(String url, String expectedBucket, String expectedRegion) {
		if (!StringUtils.hasText(url)) throw new IllegalArgumentException("empty url");

		URI u = URI.create(url);
		String host = u.getHost();
		if (!StringUtils.hasText(host)) throw new IllegalArgumentException("invalid url (no host): " + url);

		String expectedHost = expectedBucket + ".s3." + expectedRegion + ".amazonaws.com";
		if (!host.equalsIgnoreCase(expectedHost)) {
			throw new IllegalArgumentException("unsupported host: " + host);
		}

		String rawPath = u.getRawPath(); // e.g. /uploads/avatar.png
		if (!StringUtils.hasText(rawPath) || "/".equals(rawPath)) {
			throw new IllegalArgumentException("empty key in url: " + url);
		}

		String decodedKey = URLDecoder.decode(
			rawPath.startsWith("/") ? rawPath.substring(1) : rawPath,
			StandardCharsets.UTF_8
		);
		return new S3Ref(expectedBucket, decodedKey);
	}

	public static S3Ref of(String bucket, String key) {
		return new S3Ref(bucket, key);
	}
}
