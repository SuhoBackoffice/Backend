package baekgwa.suhoserver.domain.material.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.suhoserver.domain.material.dto.MaterialResponseDto;
import baekgwa.suhoserver.domain.material.facade.MaterialFacade;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.material.controller
 * FileName    : MaterialController
 * Author      : Baekgwa
 * Date        : 2025-09-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-19     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/material")
@Tag(name = "Material Controller", description = "자재 입출고 관련")
public class MaterialController {

	private final MaterialFacade materialFacade;

	@GetMapping("/{projectId}")
	@Operation(summary = "도번 혹은 품명과 일치하는 자재 정보 응답")
	public BaseResponse<List<MaterialResponseDto.MaterialInfo>> getMaterialInfo(
		@PathVariable("projectId") Long projectId,
		@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword
	) {
		List<MaterialResponseDto.MaterialInfo> findMaterialInfoList
			= materialFacade.getMaterialList(projectId, keyword);
		return BaseResponse.success(SuccessCode.GET_MATERIAL_FIND_LIST_SUCCESS, findMaterialInfoList);
	}
}
