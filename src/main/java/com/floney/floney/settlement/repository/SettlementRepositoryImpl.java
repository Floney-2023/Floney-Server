package com.floney.floney.settlement.repository;


import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.settlement.domain.entity.QSettlement.settlement;

import com.floney.floney.settlement.domain.entity.Settlement;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
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
@Transactional(readOnly = true)
public class SettlementRepositoryImpl implements SettlementCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

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

    