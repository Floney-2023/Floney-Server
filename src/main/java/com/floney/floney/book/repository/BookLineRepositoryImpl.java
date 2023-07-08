package com.floney.floney.book.repository;

import com.floney.floney.book.dto.*;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.common.constant.Status;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.floney.floney.book.dto.constant.AssetType.INCOME;
import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;
import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookLine.bookLine;
import static com.floney.floney.book.entity.QBookLineCategory.bookLineCategory;
import static com.floney.floney.book.entity.QBookUser.bookUser;
import static com.floney.floney.book.util.DateFactory.END;
import static com.floney.floney.book.util.DateFactory.START;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.user.entity.QUser.user;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.dsl.Expressions.list;

@Repository
@RequiredArgsConstructor
public class BookLineRepositoryImpl implements BookLineCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Map<String, Long> totalExpenseByMonth(String bookKey, Map<String, LocalDate> dates) {
        return jpaQueryFactory
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                bookLine.lineDate.between(dates.get(START), dates.get(END)),
                bookLineCategory.name.in(INCOME.getKind(), OUTCOME.getKind()),
                book.bookKey.eq(bookKey),
                book.status.eq(Status.ACTIVE),
                bookLine.status.eq(Status.ACTIVE)
            )
            .groupBy(bookLineCategory.name)
            .orderBy(bookLineCategory.name.asc())
            .transform(groupBy(bookLineCategory.name).as(bookLine.money.sum()));
    }

    @Override
    public List<DayLineByDayView> allLinesByDay(LocalDate date, String bookKey) {
        return jpaQueryFactory.select(
                new QDayLineByDayView(
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
                book.status.eq(Status.ACTIVE),
                bookLine.status.eq(Status.ACTIVE),
                bookLine.lineDate.eq(date),
                book.bookKey.eq(bookKey)
            )
            .groupBy(bookLine.id, bookLineCategory.name)
            .fetch();
    }

    @Override
    public List<TotalExpense> totalExpenseByDay(LocalDate date, String bookKey) {
        return jpaQueryFactory.select(
                new QTotalExpense(
                    bookLine.money.sum(),
                    bookLineCategory.name
                )
            )
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.bookLineCategories,
                bookLineCategory)
            .where(
                bookLine.status.eq(Status.ACTIVE),
                book.status.eq(Status.ACTIVE),
                bookLine.lineDate.eq(date),
                bookLineCategory.name.in(INCOME.getKind(), OUTCOME.getKind()),
                book.bookKey.eq(bookKey)
            )
            .groupBy(bookLineCategory.name)
            .orderBy(bookLineCategory.name.asc())
            .fetch();
    }

    @Override
    public List<BookLineExpense> dayIncomeAndOutcome(String bookKey, Map<String, LocalDate> dates) {
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
                bookLine.status.eq(Status.ACTIVE),
                book.status.eq(Status.ACTIVE),
                bookLine.lineDate.between(dates.get(START), dates.get(END)),
                bookLineCategory.name.in(INCOME.getKind(),
                    OUTCOME.getKind()),
                book.bookKey.eq(bookKey)
            )
            .groupBy(bookLine.lineDate, bookLineCategory.name)
            .fetch();
    }

    @Override
    public void deleteAllLines(String bookKey) {
        jpaQueryFactory.update(bookLine)
            .set(bookLine.status, INACTIVE)
            .where(bookLine.book.id.eq(
                JPAExpressions.select(book.id)
                    .from(book)
                    .where(book.bookKey.eq(bookKey))
            ))
            .execute();
    }

    @Override
    public List<DayLine> allSettlement(AllSettlementsRequest request) {
        DatesRequest dates = request.getDates();
        return jpaQueryFactory
            .select(new QDayLine(
                bookLine.id,
                bookLine.description,
                bookLine.money,
                bookLineCategory.name)
            )
            .from(bookLine)
            .innerJoin(bookLine.writer, bookUser)
            .innerJoin(bookUser.user, user)
            .leftJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                bookLine.lineDate.between(dates.start(), dates.end()),
                user.email.in(request.users()),
                bookLine.status.eq(Status.ACTIVE)
            )
            .groupBy(bookLine.id, bookLineCategory.name)
            .fetch();
    }
}
