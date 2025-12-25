package baekgwa.suhoserver.model.project.branch.serial.repository;

import org.springframework.data.jpa.repository.JpaRepository;

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
}
