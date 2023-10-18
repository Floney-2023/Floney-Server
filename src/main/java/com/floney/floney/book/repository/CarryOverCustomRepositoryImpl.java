package com.floney.floney.book.repository;

import static com.floney.floney.book.entity.QCarryOver.carryOver;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

import com.floney.floney.book.entity.Book;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CarryOverCustomRepositoryImpl implements CarryOverCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public void inactiveAllByBookKey(String bookKey) {
        jpaQueryFactory.update(carryOver)
                .set(carryOver.status, INACTIVE)
                .set(carryOver.updatedAt, LocalDateTime.now())
                .where(
                        carryOver.book.bookKey.eq(bookKey),
                        carryOver.status.eq(ACTIVE)
                )
                .execute();
    }

    @Override
    @Transactional
    public void inactiveAllByBook(final Book book) {
        jpaQueryFactory.update(carryOver)
                .set(carryOver.status, INACTIVE)
                .set(carryOver.updatedAt, LocalDateTime.now())
                .where(
                        carryOver.book.eq(book),
                        carryOver.status.eq(ACTIVE)
                )
                .execute();
    }
}
