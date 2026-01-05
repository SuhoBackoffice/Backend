package baekgwa.suhoserver.model.project.branch.serial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.project.ProductSerialState;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.serial.entity.ProjectBranchSerialEntity;

/**
 * PackageName : baekgwa.suhoserver.model.project.branch.serial.repository
 * FileName    : ProjectBranchSerialRepository
 * Author      : Baekgwa
 * Date        : 25. 12. 25.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 25.     Baekgwa               Initial creation
 */
public interface ProjectBranchSerialRepository extends JpaRepository<ProjectBranchSerialEntity, Long> {
	List<ProjectBranchSerialEntity> findByProjectBranchOrderBySequenceDesc(ProjectBranchEntity projectBranch);

	@Query("SELECT pbs FROM ProjectBranchSerialEntity pbs "
		+ "WHERE pbs.projectBranch.id = :projectId "
		+ "AND pbs.state = :state")
	List<ProjectBranchSerialEntity> findProjectBranchSerialList(
		@Param("projectId") Long projectBranchId,
		@Param("state") ProductSerialState productSerialState
	);
}
