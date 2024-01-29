package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookUser;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.floney.floney.book.domain.entity.QBook.book;
import static com.floney.floney.book.domain.entity.QBookLine.bookLine;
import static com.floney.floney.book.domain.entity.QBookLineCategory.bookLineCategory;
import static com.floney.floney.book.domain.entity.QBookUser.bookUser;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookLineCategoryRepositoryImpl implements BookLineCategoryCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public void inactiveAllByBookKey(final String bookKey) {
        final JPQLQuery<BookLine> bookLineByBookKey = JPAExpressions.selectFrom(bookLine)
            .innerJoin(bookLine.book, book)
            .where(book.bookKey.eq(bookKey));

        jpaQueryFactory.update(bookLineCategory)
            .set(bookLineCategory.status, INACTIVE)
            .set(bookLineCategory.updatedAt, LocalDateTime.now())
            .where(
                bookLineCategory.bookLine.in(bookLineByBookKey),
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
        final JPQLQuery<BookLine> bookLineByBookUser = JPAExpressions.selectFrom(bookLine)
            .innerJoin(bookLine.writer, bookUser)
            .where(bookUser.eq(targetBookUser));

        jpaQueryFactory.update(bookLineCategory)
            .set(bookLineCategory.status, INACTIVE)
            .set(bookLineCategory.updatedAt, LocalDateTime.now())
            .where(
                bookLineCategory.bookLine.in(bookLineByBookUser),
                bookLineCategory.status.eq(ACTIVE)
            )
            .execute();
    }

    @Override
    @Transactional
    public void inactiveAllByBook(final Book targetBook) {
        final JPQLQuery<BookLine> bookLineByBook = JPAExpressions.selectFrom(bookLine)
            .leftJoin(bookLine.book, book)
            .where(book.eq(targetBook));

        jpaQueryFactory.update(bookLineCategory)
            .set(bookLineCategory.status, INACTIVE)
            .set(bookLineCategory.updatedAt, LocalDateTime.now())
            .where(
                bookLineCategory.bookLine.in(bookLineByBook),
                bookLineCategory.status.eq(ACTIVE)
            )
            .execute();
    }
}
