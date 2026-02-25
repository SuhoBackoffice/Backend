package baekgwa.suhoserver.model.material.project.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import baekgwa.suhoserver.domain.material.type.MaterialStockSort;
import baekgwa.suhoserver.model.material.project.entity.ProjectMaterialStockEntity;
import baekgwa.suhoserver.model.material.project.entity.QProjectMaterialStockEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.material.project.repository
 * FileName    : ProjectMaterialStockRepositoryImpl
 * Author      : Baekgwa
 * Date        : 2026-02-25
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-02-25     Baekgwa               Initial creation
 */
@Repository
@RequiredArgsConstructor
public class ProjectMaterialStockRepositoryImpl implements ProjectMaterialStockRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private static final QProjectMaterialStockEntity stock = QProjectMaterialStockEntity.projectMaterialStockEntity;

	@Override
	public List<ProjectMaterialStockEntity> searchStockList(
		Long projectId,
		String keyword,
		MaterialStockSort sort,
		Sort.Direction dir
	) {
		BooleanBuilder whereCondition = createWhereCondition(projectId, keyword);
		OrderSpecifier<?> orderSpecifier = createOrderSpecifier(sort, dir);

		return queryFactory
			.selectFrom(stock)
			.where(whereCondition)
			.orderBy(orderSpecifier)
			.fetch();
	}

	private BooleanBuilder createWhereCondition(Long projectId, String keyword) {
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(stock.project.id.eq(projectId))
			.and(stock.totalPlanQuantity.gt(0));

		if (StringUtils.hasText(keyword)) {
			builder.and(
				stock.materialCode.containsIgnoreCase(keyword)
					.or(stock.itemName.containsIgnoreCase(keyword))
			);
		}

		return builder;
	}

	private OrderSpecifier<?> createOrderSpecifier(MaterialStockSort sort, Sort.Direction dir) {
		Order order = dir == Sort.Direction.ASC ? Order.ASC : Order.DESC;
		return switch (sort) {
			case MATERIAL_CODE -> new OrderSpecifier<>(order, stock.materialCode);
			case ITEM_NAME -> new OrderSpecifier<>(order, stock.itemName);
			case PLAN_QUANTITY -> new OrderSpecifier<>(order, stock.totalPlanQuantity);
			case INBOUND_QUANTITY -> new OrderSpecifier<>(order, stock.totalInboundQuantity);
			case USED_QUANTITY -> new OrderSpecifier<>(order, stock.totalUsedQuantity);
		};
	}
}
