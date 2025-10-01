package baekgwa.suhoserver.model.project.branch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.project.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;

/**
 * PackageName : baekgwa.suhoserver.model.project.branch.repository
 * FileName    : ProjectBranchRepository
 * Author      : Baekgwa
 * Date        : 2025-08-07
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-07     Baekgwa               Initial creation
 */
public interface ProjectBranchRepository extends JpaRepository<ProjectBranchEntity, Long> {
	List<ProjectBranchEntity> findByProject(ProjectEntity project);

	List<ProjectBranchEntity> findAllByBranchTypeIdIn(List<Long> findBranchTypeIdList);

	@Query("SELECT pb FROM ProjectBranchEntity pb JOIN FETCH pb.branchType bt WHERE pb.project = :project ORDER BY bt.code ASC")
	List<ProjectBranchEntity> findByProjectOrderByBranchCode(@Param("project") ProjectEntity project);

	@Query("SELECT pb.branchType.id FROM ProjectBranchEntity pb WHERE pb.project.id = :projectId ORDER BY pb.id ASC")
	List<Long> findIdListByProjectId(@Param("projectId") Long projectId);

	@Query("SELECT pb FROM ProjectBranchEntity pb WHERE pb.project.id = :projectId")
	List<ProjectBranchEntity> findByProjectId(@Param("projectId") Long projectId);
}
