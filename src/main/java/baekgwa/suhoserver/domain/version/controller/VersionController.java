package baekgwa.suhoserver.domain.version.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.suhoserver.domain.version.dto.VersionRequest;
import baekgwa.suhoserver.domain.version.dto.VersionResponse;
import baekgwa.suhoserver.domain.version.service.VersionService;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.version.controller
 * FileName    : VersionController
 * Author      : Baekgwa
 * Date        : 2025-08-05
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-05     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/version")
@Tag(name = "Version Controller", description = "프로젝트 및 직선/분기 레일 버전 관리용 controller")
public class VersionController {

	private final VersionService versionService;

	@PostMapping
	@Operation(summary = "신규 버전 추가")
	public BaseResponse<Void> createNewVersion(
		@RequestBody @Valid VersionRequest.NewVersionDto newVersionDto
	) {
		versionService.createNewVersion(newVersionDto);
		return BaseResponse.success(SuccessCode.CREATE_NEW_VERSION_SUCCESS);
	}

	@GetMapping
	@Operation(summary = "현재 버전 리스트 확인")
	public BaseResponse<List<VersionResponse.VersionListDto>> getVersionList() {
		List<VersionResponse.VersionListDto> versionList = versionService.getVersionList();
		return BaseResponse.success(SuccessCode.REQUEST_SUCCESS, versionList);
	}
}
