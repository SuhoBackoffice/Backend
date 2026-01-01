package baekgwa.suhoserver.model.work.report.straight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.work.report.straight.entity.WorkReportStraightSerialEntity;

/**
 * PackageName : baekgwa.suhoserver.model.work.report.straight.repository
 * FileName    : WorkReportStraightSerialRepository
 * Author      : Baekgwa
 * Date        : 25. 12. 29.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 29.     Baekgwa               Initial creation
 */
public interface WorkReportStraightSerialRepository extends JpaRepository<WorkReportStraightSerialEntity, Long> {
	boolean existsByProjectStraightSerialId(Long projectStraightSerialId);

	@Query("SELECT wrss "
		+ "FROM WorkReportStraightSerialEntity wrss "
		+ "WHERE wrss.workReportStraight.id in :straightIds")
	List<WorkReportStraightSerialEntity> findAllByStraightIds(
		@Param("straightIds") List<Long> straightIds);
}
