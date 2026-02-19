package baekgwa.suhoserver.domain.branch.service;

import java.util.List;
import java.util.stream.LongStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.model.project.ProductInactiveReason;
import baekgwa.suhoserver.model.project.ProductSerialState;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.serial.entity.ProjectBranchSerialEntity;
import baekgwa.suhoserver.model.project.branch.serial.repository.ProjectBranchSerialRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.branch.service
 * FileName    : BranchSerialWriteService
 * Author      : Baekgwa
 * Date        : 25. 12. 25.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 25.     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class BranchSerialWriteService {

	private final ProjectBranchSerialRepository projectBranchSerialRepository;

	/**
	 * 프로젝트에 할당된 분기레일의 식별 시리얼을 신규 할당
	 * @param projectBranchList 프로젝트에 신규 할당된 직선레일 리스트
	 */
	@Transactional
	public void registerProjectBranchSerial(List<ProjectBranchEntity> projectBranchList) {

		List<ProjectBranchSerialEntity> projectBranchSerialList = projectBranchList.stream()
			.flatMap(pb -> LongStream.rangeClosed(1L, pb.getTotalQuantity())
				.mapToObj(seq -> ProjectBranchSerialEntity.of(pb, seq))
			)
			.toList();

		projectBranchSerialRepository.saveAll(projectBranchSerialList);
	}

	/**
	 수량의 변동에 따라, 기존 레일 제품의 serial 의 정보를 업데이트
	 * 만약 기존보다 많이 할당되면, 새로운 serial 할당
	 * 		단, 이전에 이미 비활성화 처리되어있는 serial 이 있다면 해당 serial 부터 활성화
	 * 현재 수량보다 적다면, 내림차순으로 비활성화 처리
	 * @param findProjectBranch
	 * @param oldQuantity
	 * @param newQuantity
	 */
	@Transactional
	public void patchProjectBranchSerial(ProjectBranchEntity findProjectBranch, Long oldQuantity, Long newQuantity) {

		if (oldQuantity.equals(newQuantity))
			return;

		List<ProjectBranchSerialEntity> findSerials = projectBranchSerialRepository
			.findByProjectBranchOrderBySequenceDesc(findProjectBranch);

		if (newQuantity > oldQuantity) {
			increaseSerials(findProjectBranch, findSerials, newQuantity);
		} else {
			decreaseSerials(findSerials, newQuantity);
		}
	}

	private void decreaseSerials(List<ProjectBranchSerialEntity> findSerialList, Long newQuantity) {
		findSerialList.stream()
			.filter(b -> b.getSequence() > newQuantity)
			.filter(b -> b.getState().equals(ProductSerialState.ACTIVE))
			.forEach(b -> b.deactivate(ProductInactiveReason.DESIGN_CHANGE));
	}

	/**
	 * 추가 생산 물량 대응
	 * 시리얼을 추가적으로 활성화 혹은 생성 처리
	 * EX) 100 -> 120개로 증가 처리 시, 101 ~ 120 번의 시리얼 번호를 추가로 생성 하거나,
	 * 		101~110 까지 비활성화 되어있는 Serial 이 있다면 활성화 처리 후, 111 ~ 120 까지 추가 생성
	 * @param projectBranch
	 * @param findSerialList
	 * @param newQuantity
	 */
	private void increaseSerials(ProjectBranchEntity projectBranch, List<ProjectBranchSerialEntity> findSerialList,
		Long newQuantity) {

		findSerialList.stream()
			.filter(b -> b.getSequence() <= newQuantity)
			.filter(b -> b.getState().equals(ProductSerialState.INACTIVE))
			.forEach(ProjectBranchSerialEntity::activate);

		long maxSequence = findSerialList.stream()
			.mapToLong(ProjectBranchSerialEntity::getSequence)
			.max()
			.orElse(0L);

		if (newQuantity > maxSequence) {
			List<ProjectBranchSerialEntity> newSerials = LongStream.rangeClosed(maxSequence + 1, newQuantity)
				.mapToObj(seq -> ProjectBranchSerialEntity.of(projectBranch, seq))
				.toList();

			projectBranchSerialRepository.saveAll(newSerials);
		}
	}

	/**
	 * 특정 분기레일 전체 물량 취소로 인한 시리얼 전체 비활성화
	 * 기 생산 완료된 분기레일은 그대로 생산 상태로 둘 것
	 * @param findProjectBranch
	 */
	public void deleteProjectBranchSerial(ProjectBranchEntity findProjectBranch) {
		List<ProjectBranchSerialEntity> findBranchSerialList
			= projectBranchSerialRepository.findAllByProjectBranch(findProjectBranch);

		findBranchSerialList.forEach(bs -> bs.deactivate(ProductInactiveReason.DESIGN_CHANGE));
	}
}
