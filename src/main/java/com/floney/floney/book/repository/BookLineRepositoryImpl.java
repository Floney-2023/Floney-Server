package com.floney.floney.book.repository;

import com.floney.floney.book.dto.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.floney.floney.book.dto.constant.AssetType.INCOME;
import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;
import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookLine.bookLine;
import static com.floney.floney.book.entity.QBookLineCategory.bookLineCategory;
import static com.floney.floney.book.entity.QBookUser.bookUser;

@Repository
@RequiredArgsConstructor
public class BookLineRepositoryImpl implements BookLineCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CalendarTotalExpense> totalExpense(String bookKey, LocalDate start, LocalDate end) {
        return jpaQueryFactory.select(
                new QCalendarTotalExpense(
                    bookLine.money.sum(),
                    bookLineCategory.name
                )
            )
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                bookLine.lineDate.between(start, end),
                bookLineCategory.name.in(INCOME.getKind(), OUTCOME.getKind()),
                book.bookKey.eq(bookKey)
            )
            .groupBy(bookLineCategory.name)
            .orderBy(bookLineCategory.name.asc())
            .fetch();
    }

    @Override
    public List<DayLine> allLinesInDay(LocalDate date, String bookKey) {
        return jpaQueryFactory.select(
                new QDayLine(
                    bookLine.id,
                    bookLine.money,
                    bookLine.description,
                    bookLineCategory.name,
                    bookUser.profileImg
                ))
            .from(bookLine)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.writer, bookUser)
            .where(
                bookLine.lineDate.eq(date),
                book.bookKey.eq(bookKey)
            )
            .groupBy(bookLine.id,bookLineCategory.name)
            .fetch();
    }


    @Override
    public List<Tuple> test(LocalDate date, String bookKey) {

        return jpaQueryFactory.select(
                bookLine.id,
                bookLineCategory.name,
                bookLine.money,
                bookUser.profileImg
            )
            .from(bookLine)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.writer, bookUser)
            .where(
                bookLine.lineDate.eq(date),
                book.bookKey.eq(bookKey)
            )
            .groupBy(bookLine.id, bookLineCategory.name)
            .fetch();

    }


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
            .innerJoin(bookLine.book, book)
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

