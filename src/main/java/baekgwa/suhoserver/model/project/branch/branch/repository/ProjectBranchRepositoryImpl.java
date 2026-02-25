package baekgwa.suhoserver.model.project.branch.branch.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.branch.entity.QProjectBranchEntity;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.project.branch.branch.repository
 * FileName    : ProjectBranchRepositoryImpl
 * Author      : Baekgwa
 * Date        : 2026-02-24
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-02-24     Baekgwa               Initial creation
 */
@Repository
@RequiredArgsConstructor
public class ProjectBranchRepositoryImpl implements ProjectBranchRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private static final QProjectBranchEntity projectBranch = QProjectBranchEntity.projectBranchEntity;

	@Override
	public List<ProjectBranchEntity> findByProjectWithKeyword(ProjectEntity project, String keyword) {
		BooleanBuilder whereCondition = createWhereCondition(project, keyword);

		return queryFactory
			.selectFrom(projectBranch)
			.join(projectBranch.branchType).fetchJoin()
			.where(whereCondition)
			.orderBy(projectBranch.branchType.code.asc())
			.fetch();
	}

	private BooleanBuilder createWhereCondition(ProjectEntity project, String keyword) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(projectBranch.project.eq(project));

		if (StringUtils.hasText(keyword)) {
			builder.and(
				projectBranch.branchType.code.containsIgnoreCase(keyword)
					.or(projectBranch.branchType.name.containsIgnoreCase(keyword))
			);
		}

		return builder;
	}
}
