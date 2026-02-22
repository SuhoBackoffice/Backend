package baekgwa.suhoserver.model.material.project.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.material.project.entity.ProjectMaterialStockEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;

/**
 * PackageName : baekgwa.suhoserver.model.material.project.repository
 * FileName    : ProjectMaterialStockRepository
 * Author      : Baekgwa
 * Date        : 26. 2. 18.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 18.     Baekgwa               Initial creation
 */
public interface ProjectMaterialStockRepository extends JpaRepository<ProjectMaterialStockEntity, Long> {

	@Query("SELECT pms FROM ProjectMaterialStockEntity pms WHERE pms.project = :findProject AND pms.materialCode in :drawingNumberSet")
	List<ProjectMaterialStockEntity> findExistMaterialStockList(
		@Param("findProject") ProjectEntity findProject,
		@Param("drawingNumberSet") Collection<String> drawingNumberSet
	);

	List<ProjectMaterialStockEntity> findAllByProject(ProjectEntity project);

	@Query("SELECT pms FROM ProjectMaterialStockEntity pms WHERE pms.project = :project AND pms.id IN :ids")
	List<ProjectMaterialStockEntity> findAllByProjectAndIdIn(
		@Param("project") ProjectEntity project,
		@Param("ids") Collection<Long> ids
	);
}
