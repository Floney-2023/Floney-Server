package com.floney.floney.book.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QCarryOver.carryOver;
import static com.floney.floney.common.constant.Status.INACTIVE;

@Repository
@RequiredArgsConstructor
public class CarryOverRepositoryImpl implements CarryOverCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void deleteAllCarryOver(String bookKey) {
        jpaQueryFactory.
            update(carryOver)
            .set(carryOver.status, INACTIVE)
            .set(carryOver.updatedAt, LocalDateTime.now())
            .where(carryOver.in(
                jpaQueryFactory.selectFrom(carryOver)
                    .innerJoin(carryOver.book, book)
                    .on(book.bookKey.eq(bookKey))
            ));
    }
}
