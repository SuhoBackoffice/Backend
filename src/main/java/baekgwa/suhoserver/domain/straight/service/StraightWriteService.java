package baekgwa.suhoserver.domain.straight.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.straight.dto.StraightRequest;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.factory.StraightRailInfoFactory;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.straight.type.entity.StraightTypeEntity;
import baekgwa.suhoserver.model.straight.type.repository.StraightTypeRepository;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
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
		if (straightTypeRepository.existsByType(newStraightTypeDto.getType())) {
			throw new GlobalException(ErrorCode.DUPLICATE_STRAIGHT_TYPE);
		}

		// 2. 신규 Entity 생성 및 저장
		StraightTypeEntity newStraightType =
			StraightTypeEntity.createNewStraightType(newStraightTypeDto.getType(), newStraightTypeDto.getIsLoopRail());
		straightTypeRepository.save(newStraightType);
	}

	/**
	 * 직선레일 [가공 위치, LitzWire Sup] 정보 계산 및 반환
	 * @param postProjectStraightInfoList 직선레일 정보 DTO 리스트
	 * @param versionInfo 프로젝트 버전 정보
	 * @param straightTypeMap 미리 조회된 직선레일 타입 Map
	 * @return Map<직선레일 정보 DTO, Map<String, Object>>
	 *         Map<String, Object> 에는 "holePosition" (BigDecimal) 과 "wires" (BigDecimal[]) 가 포함됨
	 */
	@Transactional
	public Map<ProjectRequest.PostProjectStraightInfo, Map<String, Object>> calculateStraightInfo(
		List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList,
		VersionInfoEntity versionInfo,
		Map<Long, StraightTypeEntity> straightTypeMap
	) {
		Map<ProjectRequest.PostProjectStraightInfo, Map<String, Object>> result = new HashMap<>();

		for (ProjectRequest.PostProjectStraightInfo straightDto : postProjectStraightInfoList) {
			StraightTypeEntity findStraightType = straightTypeMap.get(straightDto.getStraightTypeId());
			if (findStraightType == null) {
				throw new GlobalException(ErrorCode.NOT_FOUND_STRAIGHT_TYPE);
			}

			// 가공 위치 및 LitzWire 정보 계산
			BigDecimal holePosition = StraightRailInfoFactory.calculateHolePosition(
				straightDto.getLength(),
				findStraightType.getType(),
				straightDto.getIsLoopRail(),
				versionInfo.getLoopLitzWire()
			);
			BigDecimal[] litzWireArray = StraightRailInfoFactory.generateLitzWire(
				straightDto.getLength(),
				findStraightType.getType(),
				straightDto.getIsLoopRail(),
				versionInfo.getLoopLitzWire()
			);

			Map<String, Object> info = new HashMap<>();
			info.put("holePosition", holePosition);
			info.put("wires", litzWireArray);
			result.put(straightDto, info);
		}

		return result;
	}
}
