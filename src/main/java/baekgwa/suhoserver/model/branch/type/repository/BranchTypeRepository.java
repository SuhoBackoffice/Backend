package baekgwa.suhoserver.model.branch.type.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;

/**
 * PackageName : baekgwa.suhoserver.model.branch.type.repository
 * FileName    : BranchTypeRepository
 * Author      : Baekgwa
 * Date        : 2025-08-05
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-05     Baekgwa               Initial creation
 */
public interface BranchTypeRepository extends JpaRepository<BranchTypeEntity, Long> {

	@EntityGraph(attributePaths = "versionInfoEntity")
	@Query("SELECT b FROM BranchTypeEntity b WHERE b.versionInfoEntity.id = :versionInfoId AND b.code = :code ORDER BY b.version DESC")
	List<BranchTypeEntity> findLatest(
		@Param("versionInfoId") Long versionInfoId,
		@Param("code") String code,
		Pageable pageable);

	@Query("select b from BranchTypeEntity b where b.versionInfoEntity.id = :versionInfoId and b.code = :code and b.version = :version")
	Optional<BranchTypeEntity> findBranchType(@Param("versionInfoId") Long versionInfoId, @Param("code") String code, @Param("version") LocalDate version);
}
