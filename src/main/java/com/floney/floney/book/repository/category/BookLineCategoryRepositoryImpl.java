package com.floney.floney.book.repository.category;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookLine.bookLine;
import static com.floney.floney.book.entity.QBookLineCategory.bookLineCategory;
import static com.floney.floney.book.entity.QBookUser.bookUser;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookLineCategoryRepositoryImpl implements BookLineCategoryCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public void inactiveAllByBookKey(final String bookKey) {
        final JPQLQuery<Long> bookLineByBookKey = JPAExpressions.select(bookLine.id)
                .from(bookLine)
                .leftJoin(bookLine.book, book)
                .where(book.bookKey.eq(bookKey));

        jpaQueryFactory.update(bookLineCategory)
                .set(bookLineCategory.status, INACTIVE)
                .set(bookLineCategory.updatedAt, LocalDateTime.now())
                .where(
                        bookLineCategory.bookLine.id.in(bookLineByBookKey),
                        bookLineCategory.status.eq(ACTIVE)
                )
                .execute();
    }

    @Override
    @Transactional
    public void inactiveAllByBookLineId(final Long bookLineId) {
        jpaQueryFactory.update(bookLineCategory)
                .set(bookLineCategory.status, INACTIVE)
                .set(bookLineCategory.updatedAt, LocalDateTime.now())
                .where(
                        bookLineCategory.bookLine.id.eq(bookLineId),
                        bookLineCategory.status.eq(ACTIVE)
                )
                .execute();
    }

    @Override
    @Transactional
    public void inactiveAllByBookUser(final BookUser targetBookUser) {
        final JPQLQuery<Long> bookLineByBookUser = JPAExpressions.select(bookLine.id)
                .from(bookLine)
                .innerJoin(bookLine.writer, bookUser)
                .where(bookUser.eq(targetBookUser));

        jpaQueryFactory.update(bookLineCategory)
                .set(bookLineCategory.status, INACTIVE)
                .set(bookLineCategory.updatedAt, LocalDateTime.now())
                .where(
                        bookLineCategory.bookLine.id.in(bookLineByBookUser),
                        bookLineCategory.status.eq(ACTIVE)
                )
                .execute();
    }

    @Override
    @Transactional
    public void inactiveAllByBook(final Book targetBook) {
        final JPQLQuery<Long> bookLineByBook = JPAExpressions.select(bookLine.id)
                .from(bookLine)
                .leftJoin(bookLine.book, book)
                .where(book.eq(targetBook));

        jpaQueryFactory.update(bookLineCategory)
                .set(bookLineCategory.status, INACTIVE)
                .set(bookLineCategory.updatedAt, LocalDateTime.now())
                .where(
                        bookLineCategory.bookLine.id.in(bookLineByBook),
                        bookLineCategory.status.eq(ACTIVE)
                )
                .execute();
    }
}
