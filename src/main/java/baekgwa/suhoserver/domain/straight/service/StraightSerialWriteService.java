package baekgwa.suhoserver.domain.straight.service;

import java.util.List;
import java.util.stream.LongStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.model.project.ProductInactiveReason;
import baekgwa.suhoserver.model.project.ProductSerialState;
import baekgwa.suhoserver.model.project.straight.serial.entity.ProjectStraightSerialEntity;
import baekgwa.suhoserver.model.project.straight.serial.repository.ProjectStraightSerialRepository;
import baekgwa.suhoserver.model.project.straight.straight.entity.ProjectStraightEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.straight.service
 * FileName    : StraightSerialWriteService
 * Author      : Baekgwa
 * Date        : 25. 12. 24.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 24.     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class StraightSerialWriteService {

	private final ProjectStraightSerialRepository projectStraightSerialRepository;

	/**
	 * 수량의 변동에 따라, 기존 레일 제품의 serial 의 정보를 업데이트
	 * 만약 기존보다 많이 할당되면, 새로운 serial 할당
	 * 		단, 이전에 이미 비활성화 처리되어있는 serial 이 있다면 해당 serial 부터 활성화
	 * 현재 수량보다 적다면, 내림차순으로 비활성화 처리
	 * @param projectStraight
	 * @param oldQuantity
	 * @param newQuantity
	 */
	@Transactional
	public void patchProjectStraightSerial(ProjectStraightEntity projectStraight, Long oldQuantity, Long newQuantity) {
		if (oldQuantity.equals(newQuantity))
			return;

		List<ProjectStraightSerialEntity> serials = projectStraightSerialRepository
			.findByProjectStraightOrderBySequenceDesc(projectStraight);

		if (newQuantity > oldQuantity) {
			increaseSerials(projectStraight, serials, newQuantity);
		} else {
			decreaseSerials(serials, newQuantity);
		}
	}

	/**
	 * 프로젝트에 할당된 직선레일에 serial 정보를 할당합니다.
	 * @param projectStraightList
	 */
	@Transactional
	public void registerProjectStraightSerial(List<ProjectStraightEntity> projectStraightList) {
		List<ProjectStraightSerialEntity> projectStraightSerialList = projectStraightList.stream()
			.flatMap(ps -> LongStream.rangeClosed(1L, ps.getTotalQuantity())
				.mapToObj(seq -> ProjectStraightSerialEntity.of(ps, seq))
			)
			.toList();

		projectStraightSerialRepository.saveAll(projectStraightSerialList);
	}

	private void increaseSerials(ProjectStraightEntity projectStraight, List<ProjectStraightSerialEntity> serials,
		Long newQuantity) {
		serials.stream()
			.filter(s -> s.getSequence() <= newQuantity)
			.filter(s -> s.getState() == ProductSerialState.INACTIVE)
			.forEach(ProjectStraightSerialEntity::activate);

		long maxSequence = serials.stream()
			.mapToLong(ProjectStraightSerialEntity::getSequence)
			.max()
			.orElse(0L);

		if (newQuantity > maxSequence) {
			List<ProjectStraightSerialEntity> newSerials = LongStream.rangeClosed(maxSequence + 1, newQuantity)
				.mapToObj(seq -> ProjectStraightSerialEntity.of(projectStraight, seq))
				.toList();
			projectStraightSerialRepository.saveAll(newSerials);
		}
	}

	private void decreaseSerials(List<ProjectStraightSerialEntity> serials, Long newQuantity) {
		serials.stream()
			.filter(s -> s.getSequence() > newQuantity)
			.filter(s -> s.getState() == ProductSerialState.ACTIVE)
			.forEach(s -> s.deactivate(ProductInactiveReason.DESIGN_CHANGE));
	}
}
