package baekgwa.suhoserver.model.material.inbound.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import baekgwa.suhoserver.domain.material.dto.MaterialResponse;
import baekgwa.suhoserver.domain.material.type.MaterialSort;
import baekgwa.suhoserver.model.material.inbound.entity.MaterialInboundEntity;
import baekgwa.suhoserver.model.material.inbound.entity.QMaterialInboundEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.material.inbound.repository
 * FileName    : MaterialInboundRepositoryImpl
 * Author      : Baekgwa
 * Date        : 2025-09-20
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-20     Baekgwa               Initial creation
 */
@Repository
@RequiredArgsConstructor
public class MaterialInboundRepositoryImpl implements MaterialInboundRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private static final QMaterialInboundEntity materialInbound = QMaterialInboundEntity.materialInboundEntity;

	@Override
	public List<MaterialResponse.MaterialHistory> findByProjectAndKeyword(Long projectId, String keyword,
		MaterialSort sort) {
		// 1. select 절
		DateTemplate<Date> dayExpr = Expressions.dateTemplate(Date.class, "date({0})",
			materialInbound.createdAt);
		NumberExpression<Long> cntExpr =
			materialInbound.id.count();

		// 2. where 절
		BooleanBuilder whereCondition = createMaterialHistoryWhereCondition(keyword, projectId);

		// 3. order by 절
		OrderSpecifier<Date> orderBySpec = createMaterialHistoryOrderBySpec(sort, dayExpr);

		List<Tuple> findData = queryFactory
			.select(dayExpr, cntExpr)
			.from(materialInbound)
			.where(whereCondition)
			.groupBy(dayExpr)
			.orderBy(orderBySpec)
			.fetch();

		return findData.stream()
			.map(tuple -> MaterialResponse.MaterialHistory.of(
				Objects.requireNonNull(tuple.get(dayExpr)).toLocalDate(),
				tuple.get(cntExpr)
			))
			.toList();
	}

	@Override
	public List<MaterialInboundEntity> findMaterialDetailByKeywordAndDate(
		Long projectId, String keyword, LocalDate date
	) {
		// 1. where 절
		BooleanBuilder whereCondition = createMaterialDetailWhereCondition(projectId, keyword, date);

		// 2. query
		return queryFactory
			.selectFrom(materialInbound)
			.where(whereCondition)
			.fetch();
	}

	private BooleanBuilder createMaterialDetailWhereCondition(Long projectId, String keyword, LocalDate date) {
		BooleanBuilder builder = new BooleanBuilder();

		// 1. 프로젝트 id 매칭
		builder.and(materialInbound.project.id.eq(projectId));

		// 2. keyword 매칭
		if (StringUtils.hasText(keyword)) {
			keyword = keyword.trim();
			builder.and(materialInbound.drawingNumber.containsIgnoreCase(keyword)
				.or(materialInbound.itemName.containsIgnoreCase(keyword)));
		}

		// 3. date 매칭
		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = date.plusDays(1).atStartOfDay();
		builder.and(materialInbound.createdAt.goe(start)
			.and(materialInbound.createdAt.lt(end)));

		return builder;
	}

	private OrderSpecifier<Date> createMaterialHistoryOrderBySpec(MaterialSort sort, DateTemplate<Date> dayExpr) {
		return switch (sort) {
			case LATEST -> dayExpr.desc();
			case OLDEST -> dayExpr.asc();
		};
	}

	private BooleanBuilder createMaterialHistoryWhereCondition(String keyword, Long projectId) {
		BooleanBuilder builder = new BooleanBuilder();

		// 1. 입력받은 projectId
		builder.and(materialInbound.project.id.eq(projectId));

		// 2. keyword 매칭
		if (StringUtils.hasText(keyword)) {
			keyword = keyword.trim();
			builder.and(materialInbound.drawingNumber.containsIgnoreCase(keyword)
				.or(materialInbound.itemName.containsIgnoreCase(keyword)));
		}

		return builder;
	}
}
