package baekgwa.suhoserver.domain.material.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import baekgwa.suhoserver.domain.material.type.MaterialStockSort;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.global.response.PageResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import baekgwa.suhoserver.model.material.MaterialHistoryType;
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
	@Operation(summary = "현재 프로젝트 자재 현황 응답")
	public BaseResponse<MaterialResponse.ProjectMaterialState> getProjectMaterialState(
		@PathVariable("projectId") Long projectId
	) {
		MaterialResponse.ProjectMaterialState projectMaterialState =
			materialFacade.getProjectMaterialState(projectId);

		return BaseResponse.success(SuccessCode.GET_MATERIAL_STATE_SUCCESS, projectMaterialState);
	}

	@GetMapping("/inbound/{projectId}")
	@Operation(summary = "도번 혹은 품명과 일치하는 자재 정보 응답")
	public BaseResponse<List<MaterialResponse.SearchMaterialInfo>> searchMaterialInfo(
		@PathVariable("projectId") Long projectId,
		@RequestParam(value = "keyword") String keyword
	) {
		if(keyword.length() < 2) {
			throw new GlobalException(ErrorCode.INVALID_MATERIAL_KEYWORD_OVER_2);
		}

		List<MaterialResponse.SearchMaterialInfo> searchMaterialInfoList
			= materialFacade.searchMaterialListByKeyword(projectId, keyword);
		return BaseResponse.success(SuccessCode.GET_MATERIAL_FIND_LIST_SUCCESS, searchMaterialInfoList);
	}

	@PostMapping("/inbound/{projectId}")
	@Operation(summary = "프로젝트에 자재 입고")
	public BaseResponse<Void> postMaterialInbound(
		@PathVariable("projectId") Long projectId,
		@Valid @RequestBody List<MaterialRequest.PostMaterialInbound> postMaterialInboundList,
		@AuthenticationPrincipal Long userId
	) {
		materialFacade.postMaterialInbound(projectId, postMaterialInboundList, userId);

		return BaseResponse.success(SuccessCode.POST_MATERIAL_INBOUND_UPDATE_SUCCESS);
	}

	@GetMapping("/stock/types")
	@Operation(summary = "자재 재고 목록 정렬 조건 조회")
	public BaseResponse<List<MaterialResponse.MaterialSortType>> getMaterialStockSortTypes() {
		return BaseResponse.success(SuccessCode.GET_MATERIAL_STOCK_SORT_TYPE_SUCCESS,
			materialFacade.getMaterialStockSortTypes());
	}

	@GetMapping("/stock/{projectId}")
	@Operation(summary = "프로젝트 자재 재고 목록 조회")
	public BaseResponse<List<MaterialResponse.MaterialStockInfo>> getMaterialStockList(
		@PathVariable("projectId") Long projectId,
		@RequestParam(value = "keyword", required = false) String keyword,
		@RequestParam(value = "sort", defaultValue = "MATERIAL_CODE") MaterialStockSort sort,
		@RequestParam(value = "dir", defaultValue = "ASC") Sort.Direction dir
	) {
		return BaseResponse.success(SuccessCode.GET_MATERIAL_STOCK_LIST_SUCCESS,
			materialFacade.getMaterialStockList(projectId, keyword, sort, dir));
	}

	@GetMapping("/history/types")
	@Operation(summary = "자재 이력 타입 목록 조회")
	public BaseResponse<List<MaterialResponse.MaterialHistoryTypeInfo>> getMaterialHistoryTypes() {
		return BaseResponse.success(SuccessCode.GET_MATERIAL_HISTORY_TYPE_LIST_SUCCESS,
			materialFacade.getMaterialHistoryTypes());
	}

	@GetMapping("/history/{projectId}")
	@Operation(summary = "프로젝트 자재 이력 페이징 조회")
	public BaseResponse<PageResponse<MaterialResponse.MaterialHistoryInfo>> getMaterialHistoryList(
		@PathVariable("projectId") Long projectId,
		@RequestParam(value = "keyword", required = false) String keyword,
		@RequestParam(value = "type", required = false) MaterialHistoryType type,
		@RequestParam(value = "sort", required = false, defaultValue = "LATEST") MaterialSort sort,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		@RequestParam(value = "size", required = false, defaultValue = "30") int size
	) {
		MaterialRequest.GetMaterialHistory dto
			= new MaterialRequest.GetMaterialHistory(keyword, type, sort, page, size);

		return BaseResponse.success(SuccessCode.GET_MATERIAL_HISTORY_LIST_SUCCESS,
			materialFacade.getMaterialHistoryPage(projectId, dto));
	}
}
