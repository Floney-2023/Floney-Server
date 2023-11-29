package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Book;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.floney.floney.book.domain.entity.QBook.book;
import static com.floney.floney.book.domain.entity.QCarryOver.carryOver;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CarryOverCustomRepositoryImpl implements CarryOverCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public void inactiveAllByBookKey(String bookKey) {
        final JPQLQuery<Book> bookByBookKey = JPAExpressions
                .selectFrom(book)
                .where(book.bookKey.eq(bookKey));

        jpaQueryFactory.update(carryOver)
                .set(carryOver.status, INACTIVE)
                .set(carryOver.updatedAt, LocalDateTime.now())
                .where(
                        carryOver.book.eq(bookByBookKey),
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
