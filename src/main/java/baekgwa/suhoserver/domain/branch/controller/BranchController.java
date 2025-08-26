package baekgwa.suhoserver.domain.branch.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import baekgwa.suhoserver.domain.branch.dto.BranchResponse;
import baekgwa.suhoserver.domain.branch.service.BranchService;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.branch.controller
 * FileName    : BranchController
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
@RequestMapping("/branch")
@Tag(name = "Branch Controller", description = "분기레일 컨트롤러")
public class BranchController {

	private final BranchService branchService;

	@PostMapping(value = "/bom/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "분기레일 BOM 리스트 등록")
	public BaseResponse<BranchResponse.PostNewBranchBom> newBranchBom(
		@RequestParam("branchCode") String branchCode,
		@RequestParam("versionInfoId") Long versionInfoId,
		@RequestPart("file") MultipartFile file
	) {
		BranchResponse.PostNewBranchBom postNewBranchBom =
			branchService.createNewBranchBom(branchCode, versionInfoId, file);
		return BaseResponse.success(SuccessCode.CREATE_NEW_BRANCH_BOM_SUCCESS, postNewBranchBom);
	}

	@GetMapping("/bom/latest")
	@Operation(summary = "버전, 분기별 최신 리스트 불러오기")
	public BaseResponse<BranchResponse.BranchInfoDto> getLatestVersionBranchBom(
		@RequestParam("branchCode") String branchCode,
		@RequestParam("versionInfoId") Long versionInfoId
	) {
		BranchResponse.BranchInfoDto branchInfoDto =
			branchService.getLatestVersionBranchBom(branchCode, versionInfoId);
		return BaseResponse.success(SuccessCode.GET_LATEST_BRANCH_BOM_SUCCESS, branchInfoDto);
	}

	@GetMapping("/bom/{branchTypeId}")
	@Operation(summary = "분기레일 BOM 조회")
	public BaseResponse<List<BranchResponse.BranchDetailInfoDto>> getBranchBomList(
		@PathVariable("branchTypeId") Long branchTypeId
	) {
		List<BranchResponse.BranchDetailInfoDto> branchBomList = branchService.getBranchBomList(branchTypeId);
		return BaseResponse.success(SuccessCode.GET_BRANCH_BOM_SUCCESS, branchBomList);
	}
}
