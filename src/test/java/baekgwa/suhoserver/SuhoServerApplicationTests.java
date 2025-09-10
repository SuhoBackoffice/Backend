package baekgwa.suhoserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import baekgwa.suhoserver.infra.upload.s3.S3FileClient;

@SpringBootTest
class SuhoServerApplicationTests {

	@MockitoBean
	private S3FileClient s3FileClient;

	@Test
	void contextLoads() {
	}

}
