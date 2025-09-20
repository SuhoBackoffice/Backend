package baekgwa.suhoserver.domain.material.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.suhoserver.domain.material.dto.MaterialRequest;
import baekgwa.suhoserver.domain.material.dto.MaterialResponse;
import baekgwa.suhoserver.domain.material.facade.MaterialFacade;
import baekgwa.suhoserver.domain.material.type.MaterialSort;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
	public BaseResponse<List<MaterialResponse.MaterialInfo>> getMaterialInfo(
		@PathVariable("projectId") Long projectId,
		@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword
	) {
		List<MaterialResponse.MaterialInfo> findMaterialInfoList
			= materialFacade.getMaterialList(projectId, keyword);
		return BaseResponse.success(SuccessCode.GET_MATERIAL_FIND_LIST_SUCCESS, findMaterialInfoList);
	}

	@PostMapping("/{projectId}")
	@Operation(summary = "프로젝트에 자재 입고")
	public BaseResponse<Void> postMaterialInbound(
		@PathVariable("projectId") Long projectId,
		@Valid @RequestBody List<MaterialRequest.PostMaterialInbound> postMaterialInboundList
	) {
		materialFacade.postMaterialInbound(projectId, postMaterialInboundList);

		return BaseResponse.success(SuccessCode.POST_MATERIAL_INBOUND_UPDATE_SUCCESS);
	}

	@GetMapping("/history/{projectId}")
	@Operation(summary = "키워드에 매칭되는, 자재 입고 일자 확인")
	public BaseResponse<List<MaterialResponse.MaterialHistory>> getMaterialInboundHistoryDateList(
		@PathVariable("projectId") Long projectId,
		@RequestParam(value = "keyword", required = false) String keyword,
		@RequestParam(value = "sort", required = false, defaultValue = "LATEST") MaterialSort sort
	) {
		List<MaterialResponse.MaterialHistory> materialHistoryList =
			materialFacade.getMaterialHistoryList(projectId, keyword, sort);
		return BaseResponse.success(SuccessCode.GET_MATERIAL_HISTORY_LIST_SUCCESS, materialHistoryList);
	}
}
