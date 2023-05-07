package com.floney.floney.book.repository;

import com.floney.floney.book.dto.BookLineExpense;
import com.floney.floney.book.dto.QBookLineExpense;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.floney.floney.book.entity.AssetType.INCOME;
import static com.floney.floney.book.entity.AssetType.OUTCOME;
import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookLine.bookLine;
import static com.floney.floney.book.entity.QBookLineCategory.bookLineCategory;

@Repository
@RequiredArgsConstructor
public class BookLineRepositoryImpl implements BookLineCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BookLineExpense> dayIncomeAndOutcome(String bookKey, LocalDate start, LocalDate end) {
        return jpaQueryFactory.select(
                new QBookLineExpense(
                    bookLine.lineDate,
                    bookLine.money.sum(),
                    bookLineCategory.name
                )
            )
            .from(bookLine)
            .innerJoin(bookLine.book,book)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                bookLine.lineDate.between(start, end),
                bookLineCategory.name.in(INCOME.getKind(), OUTCOME.getKind()),
                book.bookKey.eq(bookKey)
            )
            .groupBy(bookLine.lineDate, bookLineCategory.name)
            .fetch();
    }


}

