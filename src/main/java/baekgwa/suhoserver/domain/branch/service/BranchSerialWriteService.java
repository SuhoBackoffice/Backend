package baekgwa.suhoserver.domain.branch.service;

import java.util.List;
import java.util.stream.LongStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
