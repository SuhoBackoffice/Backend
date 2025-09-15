package baekgwa.suhoserver.domain.straight.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.suhoserver.domain.straight.dto.StraightRequest;
import baekgwa.suhoserver.domain.straight.dto.StraightResponse;
import baekgwa.suhoserver.domain.straight.service.StraightReadService;
import baekgwa.suhoserver.domain.straight.service.StraightWriteService;
import baekgwa.suhoserver.global.response.BaseResponse;
import baekgwa.suhoserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.straight.controller
 * FileName    : StraightController
 * Author      : Baekgwa
 * Date        : 2025-08-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-10     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/straight")
@Tag(name = "Straight Controller", description = "직선레일 컨트롤러")
public class StraightController {

	private final StraightWriteService straightWriteService;
	private final StraightReadService straightReadService;

	@PostMapping(value = "/type")
	@Operation(summary = "신규 직선레일 LizeWire 타입 등록")
	public BaseResponse<Void> postNewStraightType(
		@RequestBody @Valid StraightRequest.NewStraightTypeDto newStraightTypeDto
	) {
		straightWriteService.postNewStraightType(newStraightTypeDto);
		return BaseResponse.success(SuccessCode.CREATE_NEW_STRAIGHT_TYPE_SUCCESS);
	}

	@GetMapping(value = "/type/normal")
	@Operation(summary = "일반 직선레일 타입 조회")
	public BaseResponse<List<StraightResponse.StraightTypeDto>> getStraightTypeList() {
		List<StraightResponse.StraightTypeDto> straightTypeDtoList = straightReadService.getStraightTypeList(false);
		return BaseResponse.success(SuccessCode.GET_ALL_STRAIGHT_TYPE_LIST_SUCCESS, straightTypeDtoList);
	}

	@GetMapping(value = "/type/loop")
	@Operation(summary = "루프 직선레일 타입 조회")
	public BaseResponse<List<StraightResponse.StraightTypeDto>> getLoopStraightTypeList() {
		List<StraightResponse.StraightTypeDto> straightTypeDtoList = straightReadService.getStraightTypeList(true);
		return BaseResponse.success(SuccessCode.GET_ALL_LOOP_STRAIGHT_TYPE_LIST_SUCCESS, straightTypeDtoList);
	}
}
