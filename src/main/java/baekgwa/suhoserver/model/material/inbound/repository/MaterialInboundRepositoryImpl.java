package baekgwa.suhoserver.model.material.inbound.repository;

import java.sql.Date;
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
		BooleanBuilder whereCondition = createWhereCondition(keyword, projectId);

		// 3. order by 절
		OrderSpecifier<Date> orderBySpec = createOrderBySpec(sort, dayExpr);

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

	private OrderSpecifier<Date> createOrderBySpec(MaterialSort sort, DateTemplate<Date> dayExpr) {
		return switch (sort) {
			case LATEST -> dayExpr.desc();
			case OLDEST -> dayExpr.asc();
		};
	}

	private BooleanBuilder createWhereCondition(String keyword, Long projectId) {
		BooleanBuilder builder = new BooleanBuilder();

		// 1. 입력받은 projectId
		builder.and(materialInbound.project.id.eq(projectId));

		if (StringUtils.hasText(keyword)) {
			keyword = keyword.trim();
			builder.and(materialInbound.drawingNumber.containsIgnoreCase(keyword)
				.and(materialInbound.itemName.containsIgnoreCase(keyword)));
		}

		return builder;
	}
}
