package com.floney.floney.settlement.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.settlement.domain.entity.QSettlementUser.settlementUser;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementUserCustomRepositoryImpl implements SettlementUserCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public void inactiveAllByBookId(final long bookId) {
        jpaQueryFactory.update(settlementUser)
                .set(settlementUser.status, INACTIVE)
                .set(settlementUser.updatedAt, LocalDateTime.now())
                .where(
                        settlementUser.settlement.book.id.eq(bookId),
                        settlementUser.status.eq(ACTIVE)
                )
                .execute();
    }
}
