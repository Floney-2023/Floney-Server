package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.domain.entity.QBook;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

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
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void inactiveAllByBook(final Book book) {
        final JPQLQuery<BookLine> bookLineByBook = JPAExpressions.selectFrom(bookLine)
            .innerJoin(bookLine.book, QBook.book)
            .where(
                QBook.book.eq(book)
            )
            .where(
                bookLine.status.eq(ACTIVE)
            );

        jpaQueryFactory.update(bookLineCategory)
            .set(bookLineCategory.status, INACTIVE)
            .set(bookLineCategory.updatedAt, LocalDateTime.now())
            .where(
                bookLineCategory.bookLine.in(bookLineByBook),
                bookLineCategory.status.eq(ACTIVE)
            )
            .execute();

        entityManager.clear();
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

        entityManager.clear();
    }

    @Override
    @Transactional
    public void inactiveAllByBookUser(final BookUser targetBookUser) {
        final JPQLQuery<Long> bookLineIdByBookUser =
            JPAExpressions.select(bookLine.id)
                .from(bookLine)
                .innerJoin(bookLine.writer, bookUser)
                .where(bookUser.eq(targetBookUser),
                    bookLine.status.eq(ACTIVE));

        jpaQueryFactory.update(bookLineCategory)
            .set(bookLineCategory.status, INACTIVE)
            .set(bookLineCategory.updatedAt, LocalDateTime.now())
            .where(
                bookLineCategory.bookLine.id.in(bookLineIdByBookUser),
                bookLineCategory.status.eq(ACTIVE)
            )
            .execute();

        entityManager.clear();
    }
}
