package baekgwa.suhoserver.model.project.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;

/**
 * PackageName : baekgwa.suhoserver.model.project.project.repository
 * FileName    : ProjectRepository
 * Author      : Baekgwa
 * Date        : 2025-08-07
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-07     Baekgwa               Initial creation
 */
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
}
