package baekgwa.suhoserver.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

/**
 * PackageName : baekgwa.suhoserver.global.config
 * FileName    : SwaggerConfig
 * Author      : Baekgwa
 * Date        : 2025-08-03
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-03     Baekgwa               Initial creation
 */
@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("수호테크 프로젝트 자동화 서버 API")
				.version("v1")
				.description("수호테크 프로젝트 자동화 서버의 OpenAPI 문서입니다.")
				.contact(new Contact()
					.name("Baekgwa")
					.email("ksu9801@gmail.com")
				)
			)
			.servers(List.of(
				new Server().url("http://localhost:8080").description("로컬 서버"),
				new Server().url("https://api.suhotech.co.kr").description("배포 서버")
			));
	}
}
