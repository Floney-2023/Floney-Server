package com.floney.floney.book.repository;

import com.floney.floney.book.entity.BookLineCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static com.floney.floney.book.entity.QBookLine.bookLine;
import static com.floney.floney.book.entity.QBookLineCategory.bookLineCategory;
import static com.floney.floney.book.service.CategoryEnum.FLOW;

@Repository
@RequiredArgsConstructor
public class BookLineRepositoryImpl implements BookLineCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private final static String INCOME = "수입";
    @Override
    public Long dayIncome(String bookKey, LocalDate date) {
        return jpaQueryFactory.select(bookLine.money.sum())
            .from(bookLine)
            .where(bookLine.lineDate.eq(date))
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(bookLineCategory.name.eq(INCOME))
            .fetchOne();
    }
}
