package baekgwa.suhoserver.domain.worker.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.work.report.WorkReportStatus;
import baekgwa.suhoserver.model.work.report.report.entity.WorkReportEntity;
import baekgwa.suhoserver.model.work.report.report.repository.WorkReportRepository;
import baekgwa.suhoserver.model.work.report.straight.entity.WorkReportStraightEntity;
import baekgwa.suhoserver.model.work.report.straight.repository.WorkReportStraightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.suhoserver.domain.worker.service
 * FileName    : WorkReportReadService
 * Author      : Baekgwa
 * Date        : 25. 12. 31.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 31.     Baekgwa               Initial creation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkReportReadService {

	private final WorkReportRepository workReportRepository;
	private final WorkReportStraightRepository workReportStraightRepository;

	/**
	 * 프로젝트에 이미 보고된 직선레일 중, pending 상태로 completed 수량에 포함되지 않은 수량 조회
	 * @param findProject 프로젝트 Entity
	 * @return Map key:ProjectStraightId, value:승인 대기중 수량
	 */
	@Transactional(readOnly = true)
	public Map<Long, Long> getPendingQuantityByProjectStraight(
		ProjectEntity findProject
	) {
		List<WorkReportEntity> pendingReportList =
			workReportRepository.findByProjectAndStatus(findProject, WorkReportStatus.PENDING);

		if(pendingReportList.isEmpty()) {
			log.debug("미승인 된, 업무 보고가 없습니다.");
			return Map.of();
		}

		List<WorkReportStraightEntity> findWorkStraightList
			= workReportStraightRepository.findByWorkReportIn(pendingReportList);

		return findWorkStraightList.stream()
			.collect(Collectors.groupingBy(
				WorkReportStraightEntity::getProjectStraightId,
				Collectors.summingLong(WorkReportStraightEntity::getProductionQuantity)
			));
	}
}
