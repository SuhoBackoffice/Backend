package baekgwa.suhoserver.domain.straight.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.straight.dto.StraightResponse;
import baekgwa.suhoserver.model.straight.type.entity.StraightTypeEntity;
import baekgwa.suhoserver.model.straight.type.repository.StraightTypeRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.straight.service
 * FileName    : StraightReadService
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
public class StraightReadService {

	private final StraightTypeRepository straightTypeRepository;

	@Transactional(readOnly = true)
	public List<StraightResponse.StraightTypeDto> getStraightTypeList(boolean isLoopRail) {
		List<StraightTypeEntity> findStraightTypeList = straightTypeRepository.findByIsLoopRail(isLoopRail);

		return findStraightTypeList
			.stream().map(StraightResponse.StraightTypeDto::from)
			.toList();
	}

	/**
	 * 직선레일 PK Set 을 받아서, 조회하여 Map<PK, Entity> 반환
	 * @param straightTypeIdList 직선레일 PK Set
	 * @return Map<PK, Entity>
	 */
	@Transactional(readOnly = true)
	public Map<Long, StraightTypeEntity> getStraightTypeList(Set<Long> straightTypeIdList) {
		List<StraightTypeEntity> findStraightTypeList = straightTypeRepository.findAllById(straightTypeIdList);

		return findStraightTypeList.stream().collect(Collectors.toMap(StraightTypeEntity::getId, Function.identity()));
	}
}
