package com.floney.floney.book.repository.category;

import static com.floney.floney.book.entity.QBookLineCategory.bookLineCategory;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
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
        jpaQueryFactory.update(bookLineCategory)
                .set(bookLineCategory.status, INACTIVE)
                .set(bookLineCategory.updatedAt, LocalDateTime.now())
                .where(
                        bookLineCategory.bookLine.book.bookKey.eq(bookKey),
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
    public void inactiveAllByBookUser(final BookUser bookUser) {
        jpaQueryFactory.update(bookLineCategory)
                .set(bookLineCategory.status, INACTIVE)
                .set(bookLineCategory.updatedAt, LocalDateTime.now())
                .where(
                        bookLineCategory.bookLine.writer.eq(bookUser),
                        bookLineCategory.status.eq(ACTIVE)
                )
                .execute();
    }

    @Override
    @Transactional
    public void inactiveAllByBook(final Book book) {
        jpaQueryFactory.update(bookLineCategory)
                .set(bookLineCategory.status, INACTIVE)
                .set(bookLineCategory.updatedAt, LocalDateTime.now())
                .where(
                        bookLineCategory.bookLine.book.eq(book),
                        bookLineCategory.status.eq(ACTIVE)
                )
                .execute();
    }
}
