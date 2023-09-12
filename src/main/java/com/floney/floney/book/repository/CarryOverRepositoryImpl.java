package com.floney.floney.book.repository;

import com.floney.floney.book.entity.CarryOver;
import com.floney.floney.book.util.DateFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookUser.bookUser;
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

    @Override
    public List<CarryOver> findCarryOverHaveToDelete() {
        return jpaQueryFactory
            .selectFrom(carryOver)
            .where(carryOver.updatedAt.before(DateFactory.getThreeMonthAgo()),
                bookUser.status.eq(INACTIVE))
            .fetch();
    }
}
