package com.floney.floney.book.repository;

import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.analyze.dto.response.QAnalyzeResponseByCategory;
import com.floney.floney.book.dto.process.*;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.entity.*;
import com.floney.floney.book.util.DateFactory;
import com.floney.floney.common.constant.Status;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.floney.floney.book.dto.constant.AssetType.INCOME;
import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;
import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookLine.bookLine;
import static com.floney.floney.book.entity.QBookLineCategory.bookLineCategory;
import static com.floney.floney.book.entity.QBookUser.bookUser;
import static com.floney.floney.book.entity.QCategory.category;
import static com.floney.floney.book.entity.category.QBookCategory.bookCategory;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.user.entity.QUser.user;
import static com.querydsl.core.group.GroupBy.groupBy;

@Repository
@RequiredArgsConstructor
public class BookLineRepositoryImpl implements BookLineCustomRepository {

    private final static int DELETE_TERM = 3;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Map<String, Long> totalExpenseByMonth(String bookKey, DatesDuration dates) {
        return jpaQueryFactory
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                bookLine.lineDate.between(dates.start(), dates.end()),
                bookLineCategory.name.in(INCOME.getKind(), OUTCOME.getKind()),
                book.bookKey.eq(bookKey),
                book.status.eq(Status.ACTIVE),
                bookLine.status.eq(Status.ACTIVE)
            )
            .groupBy(bookLineCategory.name)
            .orderBy(bookLineCategory.name.asc())
            .transform(groupBy(bookLineCategory.name)
                .as(bookLine.money.sum()));
    }


    @Override
    public List<DayLineByDayView> allLinesByDay(LocalDate date, String bookKey) {
        return jpaQueryFactory.select(
                new QDayLineByDayView(
                    bookLine.id,
                    bookLine.money,
                    bookLine.description,
                    bookLineCategory.name,
                    bookUser.profileImg.coalesce(book.bookImg).as(book.bookImg),
                    bookLine.exceptStatus
                ))
            .from(bookLine)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .innerJoin(bookLine.book, book)
            .leftJoin(bookLine.writer, bookUser)
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
    public List<BookLineExpense> dayIncomeAndOutcome(String bookKey, DatesDuration dates) {
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
                bookLine.lineDate.between(dates.start(), dates.end()),
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
            .set(bookLine.updatedAt, LocalDateTime.now())
            .where(bookLine.book.id.eq(
                JPAExpressions.select(book.id)
                    .from(book)
                    .where(book.bookKey.eq(bookKey))
            ))
            .execute();
    }

    @Override
    public List<DayLine> allOutcomes(AllOutcomesRequest request) {
        DatesDuration duration = request.getDuration();
        return jpaQueryFactory
            .select(new QDayLine(
                bookLine.id,
                bookLine.description,
                bookLine.money,
                bookLineCategory.name,
                user.email)
            )
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.writer, bookUser)
            .innerJoin(bookUser.user, user)
            .leftJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                book.bookKey.eq(request.getBookKey()),
                bookLine.lineDate.between(duration.start(), duration.end()),
                user.email.in(request.users()),
                bookLine.status.eq(Status.ACTIVE)
            )
            .groupBy(bookLine.id, bookLineCategory.name)
            .fetch();
    }

    @Override
    public void deleteAllLinesByUser(BookUser bookUser, String bookKey) {
        jpaQueryFactory.update(bookLine)
            .set(bookLine.status, INACTIVE)
            .set(bookLineCategory.updatedAt, LocalDateTime.now())
            .where(bookLine.book.id.eq(
                JPAExpressions.select(book.id)
                    .from(book)
                    .where(book.bookKey.eq(bookKey))
            ), bookLine.writer.id.eq(
                bookUser.getId()
            ))
            .execute();
    }

    @Override
    public Long totalExpenseForBeforeMonth(AnalyzeByCategoryRequest request) {
        DatesDuration duration = DateFactory.getBeforeDateDuration(request.getLocalDate());
        return jpaQueryFactory
            .select(bookLine.money.sum().coalesce(0L))
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                bookLine.lineDate.between(duration.start(), duration.end()),
                bookLineCategory.name.eq(request.getRoot()),
                book.bookKey.eq(request.getBookKey()),
                book.status.eq(Status.ACTIVE),
                bookLine.status.eq(Status.ACTIVE)
            )
            .fetchOne();

    }

    @Override
    public List<AnalyzeResponseByCategory> analyzeByCategory(AnalyzeByCategoryRequest request) {
        DatesDuration datesRequest = DateFactory.getDateDuration(request.getDate());

        Category targetRoot = jpaQueryFactory.selectFrom(category)
            .where(category.name.eq(request.getRoot()),
                category.instanceOf(RootCategory.class))
            .fetchOne();

        List<String> children = jpaQueryFactory.select(category.name)
            .from(category)
            .where(category.parent.eq(targetRoot),
                category.instanceOf(DefaultCategory.class))
            .fetch();

        children.addAll(jpaQueryFactory.select(bookCategory.name)
            .from(bookCategory)
            .innerJoin(bookCategory.parent, category)
            .where(category.eq(targetRoot))
            .innerJoin(bookCategory.book, book)
            .where(book.bookKey.eq(request.getBookKey()))
            .fetch());

        return jpaQueryFactory.select(
                new QAnalyzeResponseByCategory(bookLineCategory.name,
                    bookLine.money.sum()))
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .where(book.bookKey.eq(request.getBookKey()))
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(bookLineCategory.name.in(children))
            .where(bookLine.lineDate.between(datesRequest.getStartDate(), datesRequest.getEndDate()))
            .groupBy(bookLineCategory.name)
            .fetch();
    }

    @Override
    public Long totalIncomeMoneyForBudget(AnalyzeRequestByBudget request, DatesDuration duration) {
        return totalIncomeMoney(duration, request.getBookKey());
    }

    @Override
    public Map<String, Long> totalExpensesForAsset(AnalyzeRequestByAsset request) {
        DatesDuration duration = DateFactory.getDateDuration(request.getDate());

        Map<String, Long> totalExpenses = new HashMap<>();

        Long totalIncomeMoney = totalIncomeMoney(duration, request.getBookKey());

        Long totalOutcomeMoney = jpaQueryFactory
            .select(bookLine.money.sum().coalesce(0L))
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                bookLine.lineDate.between(duration.start(), duration.end()),
                bookLineCategory.name.eq(OUTCOME.getKind()),
                book.bookKey.eq(request.getBookKey()),
                book.status.eq(Status.ACTIVE),
                bookLine.status.eq(Status.ACTIVE)
            )
            .fetchOne();

        totalExpenses.put(INCOME.getKind(), totalIncomeMoney);
        totalExpenses.put(OUTCOME.getKind(), totalOutcomeMoney);
        return totalExpenses;
    }

    @Override
    public Optional<BookLine> findByIdWithCategories(Long id) {
        return Optional.ofNullable(jpaQueryFactory
            .selectFrom(bookLine)
            .where(bookLine.id.eq(id), bookLine.status.eq(ACTIVE))
            .leftJoin(bookLine.bookLineCategories, bookLineCategory)
            .fetchJoin()
            .leftJoin(bookLine.writer, bookUser)
            .fetchJoin()
            .leftJoin(bookUser.user, user)
            .fetchJoin()
            .fetchOne());
    }

    private Long totalIncomeMoney(DatesDuration duration, String bookKey) {
        return jpaQueryFactory
            .select(bookLine.money.sum().coalesce(0L))
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                bookLine.lineDate.between(duration.start(), duration.end()),
                bookLineCategory.name.eq(INCOME.getKind()),
                book.bookKey.eq(bookKey),
                book.status.eq(Status.ACTIVE),
                bookLine.status.eq(Status.ACTIVE),
                bookLine.exceptStatus.eq(false)
            )
            .fetchOne();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLine> findLineHaveToDelete() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(DELETE_TERM);
        return jpaQueryFactory
            .selectFrom(bookLine)
            .where(bookLine.updatedAt.before(threeMonthsAgo),
                bookLine.status.eq(INACTIVE))
            .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLineCategory> findCategoryHaveToDelete() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(DELETE_TERM);
        return jpaQueryFactory
            .selectFrom(bookLineCategory)
            .where(bookLineCategory.updatedAt.before(threeMonthsAgo),
                bookLineCategory.status.eq(INACTIVE))
            .fetch();
    }

}
