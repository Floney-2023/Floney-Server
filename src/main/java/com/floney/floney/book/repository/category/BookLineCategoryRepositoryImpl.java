package com.floney.floney.book.repository.category;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookLine.bookLine;
import static com.floney.floney.book.entity.QBookLineCategory.bookLineCategory;
import static com.floney.floney.common.constant.Status.INACTIVE;

@Repository
@RequiredArgsConstructor
public class BookLineCategoryRepositoryImpl implements BookLineCategoryCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void deleteBookLineCategory(String bookKey) {
        jpaQueryFactory.update(bookLineCategory)
            .set(bookLineCategory.status, INACTIVE)
            .set(bookLineCategory.updatedAt, LocalDateTime.now())
            .where(bookLineCategory.bookLine.id.in(
                JPAExpressions.select(bookLine.id)
                    .from(bookLine)
                    .innerJoin(bookLine.book, book)
                    .where(book.bookKey.eq(bookKey))
            ))
            .execute();
    }

    @Override
    public void deleteBookLineCategoryById(Long id) {
        jpaQueryFactory.update(bookLineCategory)
            .set(bookLineCategory.status, INACTIVE)
            .set(bookLineCategory.updatedAt, LocalDateTime.now())
            .where(bookLineCategory.bookLine.id.eq(id))
            .execute();
    }
}
