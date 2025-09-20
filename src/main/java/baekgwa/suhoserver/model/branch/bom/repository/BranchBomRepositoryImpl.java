package baekgwa.suhoserver.model.branch.bom.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import baekgwa.suhoserver.model.branch.bom.entity.BranchBomEntity;
import baekgwa.suhoserver.model.branch.bom.entity.QBranchBomEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.branch.bom.repository
 * FileName    : BranchBomRepositoryImpl
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
public class BranchBomRepositoryImpl implements BranchBomRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private static final QBranchBomEntity branchBom = QBranchBomEntity.branchBomEntity;

	@Override
	public List<BranchBomEntity> searchBranchBomList(List<Long> findBranchTypeIdList, String keyword) {
		// 1. Where
		BooleanBuilder whereCondition = createWhereCondition(findBranchTypeIdList, keyword);

		// 2. find
		return queryFactory
			.selectFrom(branchBom)
			.where(whereCondition)
			.fetch();
	}

	private BooleanBuilder createWhereCondition(List<Long> findBranchTypeIdList, String keyword) {
		BooleanBuilder builder = new BooleanBuilder();

		if(!findBranchTypeIdList.isEmpty()) {
			builder.and(branchBom.branchTypeEntity.id.in(findBranchTypeIdList));
		}

		if(StringUtils.hasText(keyword)) {
			builder.and(
				branchBom.drawingNumber.containsIgnoreCase(keyword)
					.or(branchBom.itemName.containsIgnoreCase(keyword))
			);
		}

		return builder;
	}
}
