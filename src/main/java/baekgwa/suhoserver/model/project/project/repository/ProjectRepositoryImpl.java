package baekgwa.suhoserver.model.project.project.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.project.dto.ProjectResponse;
import baekgwa.suhoserver.domain.project.type.ProjectSort;
import baekgwa.suhoserver.model.project.project.entity.QProjectEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.project.project.repository
 * FileName    : ProjectRepositoryImpl
 * Author      : Baekgwa
 * Date        : 2025-08-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-19     Baekgwa               Initial creation
 */
@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private static final QProjectEntity project = QProjectEntity.projectEntity;

	@Override
	public Page<ProjectResponse.ProjectInfo> searchProjectList(ProjectRequest.GetProjectInfo dto) {
		// 1. 페이지 객체 생성
		Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize());

		// 1-1. where 조건절 생성
		BooleanBuilder whereCondition = createWhereCondition(dto);

		// 1-2. order 조건절 생성
		OrderSpecifier<LocalDate> orderSpecifier = createOrderSpecifier(dto.getSort());

		// 2. query find 진행
		List<ProjectResponse.ProjectInfo> findProjectList = queryFactory
			.selectFrom(project)
			.where(whereCondition)
			.orderBy(orderSpecifier)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch()
			.stream()
			.map(ProjectResponse.ProjectInfo::of)
			.toList();

		// 3. 페이징을 위한, 전체 수량 조회
		Long totalCount = queryFactory
			.select(project.count())
			.from(project)
			.where(whereCondition)
			.fetchOne();

		return new PageImpl<>(findProjectList, pageable, totalCount != null ? totalCount : 0);
	}

	private BooleanBuilder createWhereCondition(ProjectRequest.GetProjectInfo dto) {
		BooleanBuilder builder = new BooleanBuilder();

		// 1. 프로젝트명 keyword 필터링
		String keyword = dto.getKeyword();
		if (StringUtils.hasText(keyword)) {
			builder.and(project.name.containsIgnoreCase(keyword));
		}

		// 2. version 정보 필터링
		Long versionId = dto.getVersionId();
		if (versionId != null) {
			builder.and(project.versionInfoEntity.id.eq(versionId));
		}

		// 3. 기간 필터링
		LocalDate startDate = dto.getStartDate();
		LocalDate endDate = dto.getEndDate();
		if (startDate != null) {
			builder.and(project.startDate.goe(startDate));
		}

		if (endDate != null) {
			builder.and(project.endDate.loe(endDate));
		}

		return builder;
	}

	private OrderSpecifier<LocalDate> createOrderSpecifier(ProjectSort sort) {
		return switch (sort) {
			case START_DATE -> new OrderSpecifier<>(Order.ASC, project.startDate);
			case END_DATE -> new OrderSpecifier<>(Order.ASC, project.endDate);
		};
	}
}
