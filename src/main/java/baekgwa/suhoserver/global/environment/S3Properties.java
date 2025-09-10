package baekgwa.suhoserver.global.environment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * PackageName : baekgwa.suhoserver.global.environment
 * FileName    : S3Properties
 * Author      : Baekgwa
 * Date        : 2025-09-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-10     Baekgwa               Initial creation
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "s3")
public class S3Properties {
	private String bucket;
	private String region;
	private String accessKey;
	private String secretKey;
}
