package com.floney.floney.settlement.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.settlement.domain.entity.QSettlement.settlement;
import static com.floney.floney.settlement.domain.entity.QSettlementUser.settlementUser;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementUserCustomRepositoryImpl implements SettlementUserCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void inactiveAllByBookId(final long bookId) {
        final JPQLQuery<Long> settlementByBookId = JPAExpressions
            .select(settlement.id)
            .from(settlement)
            .where(settlement.book.id.eq(bookId));

        jpaQueryFactory.update(settlementUser)
            .set(settlementUser.status, INACTIVE)
            .set(settlementUser.updatedAt, LocalDateTime.now())
            .where(
                settlementUser.settlement.id.in(settlementByBookId),
                settlementUser.status.eq(ACTIVE)
            )
            .execute();

        entityManager.clear();
    }
}
