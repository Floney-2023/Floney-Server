package com.floney.floney.settlement.repository;

import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.settlement.domain.entity.QSettlementUser.settlementUser;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
