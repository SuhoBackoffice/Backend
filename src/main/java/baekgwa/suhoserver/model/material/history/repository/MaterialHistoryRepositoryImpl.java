package baekgwa.suhoserver.model.material.history.repository;

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

import baekgwa.suhoserver.domain.material.dto.MaterialRequest;
import baekgwa.suhoserver.domain.material.dto.MaterialResponse;
import baekgwa.suhoserver.domain.material.type.MaterialSort;
import baekgwa.suhoserver.model.material.history.entity.QMaterialHistoryEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.material.history.repository
 * FileName    : MaterialHistoryRepositoryImpl
 * Author      : Baekgwa
 * Date        : 26. 2. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 2. 23.     Baekgwa               Initial creation
 */
@Repository
@RequiredArgsConstructor
public class MaterialHistoryRepositoryImpl implements MaterialHistoryRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private static final QMaterialHistoryEntity history = QMaterialHistoryEntity.materialHistoryEntity;

	@Override
	public Page<MaterialResponse.MaterialHistoryInfo> searchHistoryList(
		Long projectId,
		MaterialRequest.GetMaterialHistory dto
	) {
		Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize());
		BooleanBuilder whereCondition = createWhereCondition(projectId, dto);
		OrderSpecifier<?> orderSpecifier = createOrderSpecifier(dto.getSort());

		List<MaterialResponse.MaterialHistoryInfo> content = queryFactory
			.selectFrom(history)
			.where(whereCondition)
			.orderBy(orderSpecifier)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch()
			.stream()
			.map(MaterialResponse.MaterialHistoryInfo::of)
			.toList();

		Long totalCount = queryFactory
			.select(history.count())
			.from(history)
			.where(whereCondition)
			.fetchOne();

		return new PageImpl<>(content, pageable, totalCount != null ? totalCount : 0);
	}

	private BooleanBuilder createWhereCondition(Long projectId, MaterialRequest.GetMaterialHistory dto) {
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(history.project.id.eq(projectId));

		if (StringUtils.hasText(dto.getKeyword())) {
			builder.and(
				history.materialCode.containsIgnoreCase(dto.getKeyword())
					.or(history.itemName.containsIgnoreCase(dto.getKeyword()))
			);
		}

		if (dto.getType() != null) {
			builder.and(history.type.eq(dto.getType()));
		}

		return builder;
	}

	private OrderSpecifier<?> createOrderSpecifier(MaterialSort sort) {
		return switch (sort) {
			case LATEST -> new OrderSpecifier<>(Order.DESC, history.createdAt);
			case OLDEST -> new OrderSpecifier<>(Order.ASC, history.createdAt);
		};
	}
}
