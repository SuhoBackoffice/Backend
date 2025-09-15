package baekgwa.suhoserver.domain.straight.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.straight.dto.StraightRequest;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.straight.type.entity.StraightTypeEntity;
import baekgwa.suhoserver.model.straight.type.repository.StraightTypeRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.straight.service
 * FileName    : StraightWriteService
 * Author      : Baekgwa
 * Date        : 2025-09-15
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-15     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class StraightWriteService {

	private final StraightTypeRepository straightTypeRepository;

	@Transactional
	public void postNewStraightType(StraightRequest.NewStraightTypeDto newStraightTypeDto) {
		// 1. 중복 확인
		if(straightTypeRepository.existsByType(newStraightTypeDto.getType())) {
			throw new GlobalException(ErrorCode.DUPLICATE_STRAIGHT_TYPE);
		}

		// 2. 신규 Entity 생성 및 저장
		StraightTypeEntity newStraightType =
			StraightTypeEntity.createNewStraightType(newStraightTypeDto.getType(), newStraightTypeDto.getIsLoopRail());
		straightTypeRepository.save(newStraightType);
	}
}
