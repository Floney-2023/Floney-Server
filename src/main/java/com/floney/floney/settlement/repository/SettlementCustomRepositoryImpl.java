package com.floney.floney.settlement.repository;


import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.settlement.domain.entity.QSettlement.settlement;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementCustomRepositoryImpl implements SettlementCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public void inactiveAllByBookKey(final String bookKey) {
        jpaQueryFactory.update(settlement)
                .set(settlement.status, INACTIVE)
                .set(settlement.updatedAt, LocalDateTime.now())
                .where(
                        settlement.book.bookKey.eq(bookKey),
                        settlement.status.eq(ACTIVE)
                )
                .execute();
    }

    @Override
    @Transactional
    public void inactiveAllByBookId(final long bookId) {
        jpaQueryFactory.update(settlement)
                .set(settlement.status, INACTIVE)
                .set(settlement.updatedAt, LocalDateTime.now())
                .where(
                        settlement.book.id.eq(bookId),
                        settlement.status.eq(ACTIVE)
                )
                .execute();
    }
}

