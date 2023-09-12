package com.floney.floney.settlement.repository;

import com.floney.floney.settlement.domain.entity.Settlement;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.settlement.domain.entity.QSettlement.settlement;

@Repository
@RequiredArgsConstructor
public class SettlementRepositoryImpl implements SettlementCustomRepository {
    private final static int DELETE_TERM = 3;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional(readOnly = true)
    public List<Settlement> findSettlementHaveToDelete() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(DELETE_TERM);
        return jpaQueryFactory
            .selectFrom(settlement)
            .where(settlement.updatedAt.before(threeMonthsAgo),
                settlement.status.eq(INACTIVE))
            .fetch();
    }


    @Override
    @Transactional
    public void deleteAllSettlement(String bookKey) {
        jpaQueryFactory.
            update(settlement)
            .set(settlement.status, INACTIVE)
            .set(settlement.updatedAt, LocalDateTime.now())
            .where(settlement.in(
                jpaQueryFactory.selectFrom(settlement)
                    .innerJoin(settlement.book, book)
                    .on(book.bookKey.eq(bookKey))
            ));


    }
}
