package com.floney.floney.book.repository;

import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.analyze.dto.response.QAnalyzeResponseByCategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.domain.entity.Category;
import com.floney.floney.book.dto.process.*;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.util.DateFactory;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
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

import static com.floney.floney.book.domain.entity.QBook.book;
import static com.floney.floney.book.domain.entity.QBookLine.bookLine;
import static com.floney.floney.book.domain.entity.QBookLineCategory.bookLineCategory;
import static com.floney.floney.book.domain.entity.QBookUser.bookUser;
import static com.floney.floney.book.dto.constant.AssetType.*;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.user.entity.QUser.user;
import static com.querydsl.core.group.GroupBy.groupBy;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookLineRepositoryImpl implements BookLineCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Map<String, Double> totalExpenseByMonth(String bookKey, DatesDuration dates) {
        return jpaQueryFactory
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                bookLine.lineDate.between(dates.start(), dates.end()),
                bookLineCategory.name.in(INCOME.getKind(), OUTCOME.getKind()),
                book.bookKey.eq(bookKey),
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE)
            )
            .groupBy(bookLineCategory.name)
            .orderBy(bookLineCategory.name.asc())
            .transform(
                groupBy(bookLineCategory.name).as(bookLine.money.sum())
            );
    }

    @Override
    public List<BookLine> findAllBookLineByDuration(String bookKey, DatesDuration duration) {
        return jpaQueryFactory
            .selectFrom(bookLine)
            .innerJoin(bookLine.book, book)
            .where(
                bookLine.lineDate.between(duration.start(), duration.end()),
                book.bookKey.eq(bookKey),
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE)
            )
            .fetch();
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
                    bookLine.exceptStatus,
                    user.nickname
                ))
            .from(bookLine)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .innerJoin(bookLine.book, book)
            .leftJoin(bookLine.writer, bookUser)
            .leftJoin(bookUser.user, user)
            .where(
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLine.lineDate.eq(date),
                book.bookKey.eq(bookKey),
                bookUser.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE)
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
                ))
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                bookLine.status.eq(ACTIVE),
                book.status.eq(ACTIVE),
                bookLine.lineDate.eq(date),
                bookLineCategory.name.in(
                    INCOME.getKind(),
                    OUTCOME.getKind()
                ),
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
                bookLine.status.eq(ACTIVE),
                book.status.eq(ACTIVE),
                bookLine.lineDate.between(dates.start(), dates.end()),
                bookLineCategory.name.in(
                    INCOME.getKind(),
                    OUTCOME.getKind()
                ),
                book.bookKey.eq(bookKey)
            )
            .groupBy(bookLine.lineDate, bookLineCategory.name)
            .fetch();
    }

    @Override
    @Transactional
    public void inactiveAllBy(String bookKey) {
        final JPQLQuery<Book> bookByBookKey = JPAExpressions
            .selectFrom(book)
            .where(book.bookKey.eq(bookKey));

        jpaQueryFactory.update(bookLine)
            .set(bookLine.status, INACTIVE)
            .set(bookLine.updatedAt, LocalDateTime.now())
            .where(
                bookLine.book.eq(bookByBookKey),
                bookLine.status.eq(ACTIVE)
            )
            .execute();
    }

    @Override
    public List<DayLine> getAllLines(AllOutcomesRequest request) {
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
                bookUser.status.eq(ACTIVE),
                user.status.eq(ACTIVE),
                book.bookKey.eq(request.getBookKey()),
                bookLine.lineDate.between(duration.start(), duration.end()),
                user.email.in(request.getUsersEmails()),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE)
            )
            .groupBy(bookLine.id, bookLineCategory.name)
            .fetch();
    }

    @Override
    public Double totalExpenseForBeforeMonth(AnalyzeByCategoryRequest request) {
        DatesDuration duration = DateFactory.getLastMonthDateDuration(request.getLocalDate());
        return jpaQueryFactory
            .select(bookLine.money.sum().coalesce(0.0))
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                bookLine.lineDate.between(duration.start(), duration.end()),
                bookLineCategory.name.eq(request.getRoot()),
                book.bookKey.eq(request.getBookKey()),
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE)
            )
            .fetchOne();
    }

    @Override
    public List<AnalyzeResponseByCategory> analyzeByCategory(List<Category> childCategories, DatesDuration duration, String bookKey) {
        return jpaQueryFactory.select(
                new QAnalyzeResponseByCategory(bookLineCategory.name, bookLine.money.sum()))
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .where(book.bookKey.eq(bookKey))
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(bookLineCategory.category.in(childCategories),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE))
            .where(bookLine.lineDate.between(duration.getStartDate(), duration.getEndDate()))
            .groupBy(bookLineCategory.name)
            .fetch();
    }

    @Override
    public Double totalOutcomeMoneyForBudget(AnalyzeRequestByBudget request, DatesDuration duration) {
        return totalOutcomeMoney(duration, request.getBookKey());
    }

    @Override
    public Map<String, Double> totalExpensesForAsset(AnalyzeRequestByAsset request) {
        DatesDuration duration = DateFactory.getStartAndEndOfMonth(request.getDate());

        Map<String, Double> totalExpenses = new HashMap<>();

        double totalIncomeMoney = totalIncomeMoney(duration, request.getBookKey());

        double totalOutcomeMoney = totalOutcomeMoney(duration, request.getBookKey());

        totalExpenses.put(INCOME.getKind(), totalIncomeMoney);
        totalExpenses.put(OUTCOME.getKind(), totalOutcomeMoney);
        return totalExpenses;
    }

    @Override
    public Optional<BookLine> findByIdWithCategories(Long id) {
        return Optional.ofNullable(jpaQueryFactory
            .selectFrom(bookLine)
            .where(
                bookLine.id.eq(id),
                bookLine.status.eq(ACTIVE),
                user.status.eq(ACTIVE),
                bookUser.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE)
            )
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .fetchJoin()
            .innerJoin(bookLine.writer, bookUser).fetchJoin()
            .leftJoin(bookUser.user, user).fetchJoin()
            .fetchOne()
        );
    }

    @Override
    public List<BookLine> findAllByBook(final String bookKey) {
        return jpaQueryFactory
            .selectFrom(bookLine)
            .innerJoin(bookLine.book, book).fetchJoin()
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .innerJoin(bookLine.writer, bookUser).fetchJoin()
            .innerJoin(bookUser.user, user).fetchJoin()
            .where(
                bookLineCategory.name.in(INCOME.getKind(), OUTCOME.getKind(), BANK.getKind()),
                book.bookKey.eq(bookKey),
                book.status.eq(ACTIVE),
                bookUser.status.eq(ACTIVE),
                user.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE)
            )
            .fetch();
    }

    @Override
    @Transactional
    public void inactiveAllByBook(final Book book) {
        jpaQueryFactory.update(bookLine)
            .set(bookLine.status, INACTIVE)
            .set(bookLine.updatedAt, LocalDateTime.now())
            .where(
                bookLine.book.eq(book),
                bookLine.status.eq(ACTIVE)
            ).execute();
    }

    @Override
    public List<BookLine> findAllByBookUser(BookUser bookUser) {
        return jpaQueryFactory.selectFrom(bookLine)
            .where(bookLine.writer.eq(bookUser), bookLine.status.eq(ACTIVE))
            .fetch();
    }

    private Double totalIncomeMoney(DatesDuration duration, String bookKey) {
        return jpaQueryFactory
            .select(bookLine.money.sum().coalesce(0.0))
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                bookLine.lineDate.between(duration.start(), duration.end()),
                bookLineCategory.name.eq(INCOME.getKind()),
                book.bookKey.eq(bookKey),
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLine.exceptStatus.eq(false)
            )
            .fetchOne();
    }

    private Double totalOutcomeMoney(DatesDuration duration, String bookKey) {
        return jpaQueryFactory
            .select(bookLine.money.sum().coalesce(0.0))
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.bookLineCategories, bookLineCategory)
            .where(
                bookLine.lineDate.between(duration.start(), duration.end()),
                bookLineCategory.name.eq(OUTCOME.getKind()),
                book.bookKey.eq(bookKey),
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLine.exceptStatus.eq(false)
            )
            .fetchOne();
    }
}
