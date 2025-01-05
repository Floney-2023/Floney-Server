package com.floney.floney.book.repository;

import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.analyze.dto.response.QAnalyzeResponseByCategory;
import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.domain.entity.RepeatBookLine;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.BookLineWithWriterView;
import com.floney.floney.book.dto.process.TotalExpense;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.common.domain.vo.DateDuration;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.floney.floney.book.domain.category.CategoryType.INCOME;
import static com.floney.floney.book.domain.category.CategoryType.OUTCOME;
import static com.floney.floney.book.domain.category.entity.QCategory.category;
import static com.floney.floney.book.domain.category.entity.QSubcategory.subcategory;
import static com.floney.floney.book.domain.entity.QBook.book;
import static com.floney.floney.book.domain.entity.QBookLine.bookLine;
import static com.floney.floney.book.domain.entity.QBookLineCategory.bookLineCategory;
import static com.floney.floney.book.domain.entity.QBookUser.bookUser;
import static com.floney.floney.book.domain.entity.QRepeatBookLine.repeatBookLine;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.user.entity.QUser.user;

@Repository
@Transactional
@RequiredArgsConstructor
public class BookLineRepositoryImpl implements BookLineCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<BookLine> findAllByDurationOrderByDateDesc(final String bookKey,
                                                           final DateDuration duration) {
        return jpaQueryFactory.selectFrom(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.categories, bookLineCategory).fetchJoin()
            .innerJoin(bookLineCategory.lineCategory, category).fetchJoin()
            .innerJoin(bookLineCategory.lineSubcategory, subcategory).fetchJoin()
            .innerJoin(bookLineCategory.assetSubcategory, subcategory).fetchJoin()
            .where(
                book.bookKey.eq(bookKey),
                bookLine.lineDate.between(duration.getStartDate(), duration.getEndDate())
            )
            .where(
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineCategory.status.eq(ACTIVE),
                bookLineCategory.lineSubcategory.status.eq(ACTIVE),
                bookLineCategory.assetSubcategory.status.eq(ACTIVE)
            )
            .orderBy(bookLine.lineDate.desc())
            .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLineWithWriterView> allLinesByDay(final LocalDate date, final String bookKey) {
        return jpaQueryFactory.select(
                new QBookLineWithWriterView(
                    bookLine.id,
                    bookLine.money,
                    bookLine.description,
                    bookLine.exceptStatus,
                    bookLineCategory.lineCategory.name,
                    bookLineCategory.lineSubcategory.name,
                    bookLineCategory.assetSubcategory.name,
                    user.email,
                    user.nickname,
                    bookUser.profileImg.coalesce(book.bookImg).as(book.bookImg),
                    repeatBookLine.repeatDuration.coalesce(Expressions.asString(String.valueOf(RepeatDuration.NONE))).as(repeatBookLine.repeatDuration)
                ))
            .from(bookLine)
            .innerJoin(bookLine.categories, bookLineCategory)
            .innerJoin(bookLineCategory.lineCategory, category)
            .innerJoin(bookLineCategory.lineSubcategory, subcategory)
            .innerJoin(bookLineCategory.assetSubcategory, subcategory)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.writer, bookUser)
            .innerJoin(bookUser.user, user)
            .leftJoin(bookLine.repeatBookLine, repeatBookLine)
            .where(
                bookLine.lineDate.eq(date),
                book.bookKey.eq(bookKey)
            )
            .where(
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookUser.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                user.status.eq(ACTIVE),
                bookLineCategory.lineCategory.status.eq(ACTIVE),
                bookLineCategory.lineSubcategory.status.eq(ACTIVE),
                bookLineCategory.assetSubcategory.status.eq(ACTIVE)
            )
            .orderBy(bookLine.createdAt.desc())
            .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public TotalExpense totalMoneyByDateAndCategoryType(final String bookKey,
                                                        final LocalDate date,
                                                        final CategoryType categoryType) {
        return Optional.ofNullable(jpaQueryFactory.select(
                    new QTotalExpense(
                        bookLineCategory.lineCategory.name,
                        bookLine.money.sum().coalesce(0.0)
                    ))
                .from(bookLine)
                .innerJoin(bookLine.book, book)
                .innerJoin(bookLine.categories, bookLineCategory)
                .innerJoin(bookLineCategory.lineCategory, category)
                .where(
                    book.bookKey.eq(bookKey),
                    bookLineCategory.lineCategory.name.eq(categoryType),
                    bookLine.lineDate.eq(date)
                )
                .where(
                    bookLine.status.eq(ACTIVE),
                    book.status.eq(ACTIVE),
                    bookLineCategory.status.eq(ACTIVE),
                    bookLineCategory.lineCategory.status.eq(ACTIVE)
                )
                .groupBy(bookLineCategory.lineCategory.name)
                .fetchOne())
            .orElse(new TotalExpense(categoryType, 0.0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLineExpense> findIncomeAndOutcomeByDurationPerDay(final String bookKey,
                                                                      final DateDuration dates) {
        return jpaQueryFactory.select(
                new QBookLineExpense(
                    bookLine.lineDate,
                    bookLine.money.sum().coalesce(0.0),
                    bookLineCategory.lineCategory.name
                )
            )
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.categories, bookLineCategory)
            .innerJoin(bookLineCategory.lineCategory, category)
            .where(
                book.bookKey.eq(bookKey),
                bookLine.lineDate.between(dates.getStartDate(), dates.getEndDate()),
                bookLineCategory.lineCategory.name.in(INCOME, OUTCOME)
            )
            .where(
                bookLine.status.eq(ACTIVE),
                book.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineCategory.status.eq(ACTIVE)
            )
            .groupBy(bookLine.lineDate, bookLineCategory.lineCategory.name)
            .fetch();
    }

    @Override
    public void inactiveAllBy(final Book book) {
        jpaQueryFactory.update(bookLine)
            .set(bookLine.status, INACTIVE)
            .set(bookLine.updatedAt, LocalDateTime.now())
            .where(
                bookLine.book.eq(book),
                bookLine.status.eq(ACTIVE)
            )
            .execute();

        entityManager.clear();
    }

    @Override
    public List<BookLine> findAllRepeatBookLineByEqualOrAfter(final LocalDate localDate, final RepeatBookLine repeatBookLine) {
        return jpaQueryFactory.selectFrom(bookLine)
            .where(
                bookLine.lineDate.after(localDate).or(bookLine.lineDate.eq(localDate)),
                bookLine.repeatBookLine.eq(repeatBookLine),
                bookLine.status.eq(ACTIVE))
            .fetch();
    }


    @Override
    public List<BookLine> findAllRepeatBookLine(final RepeatBookLine repeatBookLine) {
        return jpaQueryFactory.selectFrom(bookLine)
            .where(
                bookLine.repeatBookLine.eq(repeatBookLine),
                bookLine.status.eq(ACTIVE))
            .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLineWithWriterView> findAllOutcomes(final AllOutcomesRequest request) {
        final DateDuration duration = request.getDuration();
        return jpaQueryFactory.select(
                new QBookLineWithWriterView(
                    bookLine.id,
                    bookLine.money,
                    bookLine.description,
                    bookLine.exceptStatus,
                    bookLineCategory.lineCategory.name,
                    bookLineCategory.lineSubcategory.name,
                    bookLineCategory.assetSubcategory.name,
                    user.email,
                    user.nickname,
                    user.profileImg,
                    repeatBookLine.repeatDuration.coalesce(Expressions.asString(String.valueOf(RepeatDuration.NONE))).as(repeatBookLine.repeatDuration)
                )
            )
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.writer, bookUser)
            .innerJoin(bookUser.user, user)
            .innerJoin(bookLine.categories, bookLineCategory)
            .innerJoin(bookLineCategory.lineCategory, category)
            .innerJoin(bookLineCategory.lineSubcategory, subcategory)
            .innerJoin(bookLineCategory.assetSubcategory, subcategory)
            .leftJoin(bookLine.repeatBookLine, repeatBookLine)
            .where(
                book.bookKey.eq(request.getBookKey()),
                bookLine.lineDate.between(duration.getStartDate(), duration.getEndDate()),
                bookLineCategory.lineCategory.name.eq(OUTCOME),
                user.email.in(request.getUsersEmails())
            )
            .where(
                bookUser.status.eq(ACTIVE),
                user.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineCategory.status.eq(ACTIVE),
                bookLineCategory.lineSubcategory.status.eq(ACTIVE),
                bookLineCategory.assetSubcategory.status.eq(ACTIVE)
            )
            .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public double totalExpenseForBeforeMonth(final AnalyzeByCategoryRequest request) {
        final DateDuration duration = DateDuration.firstAndLastDayFromLastMonth(request.getLocalDate());
        final CategoryType categoryType = CategoryType.findLineByMeaning(request.getRoot());

        return Optional.ofNullable(jpaQueryFactory.select(bookLine.money.sum())
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.categories, bookLineCategory)
            .innerJoin(bookLineCategory.lineCategory, category)
            .where(
                bookLine.lineDate.between(duration.getStartDate(), duration.getEndDate()),
                bookLineCategory.lineCategory.name.eq(categoryType),
                book.bookKey.eq(request.getBookKey())
            )
            .where(
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineCategory.status.eq(ACTIVE)
            )
            .fetchOne()).orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyzeResponseByCategory> analyzeByLineSubcategory(final List<Subcategory> childCategories,
                                                                    final DateDuration duration,
                                                                    final String bookKey) {
        for (final Subcategory subcategory : childCategories) {
            validateLineSubcategory(subcategory);
        }

        return jpaQueryFactory.select(
                new QAnalyzeResponseByCategory(
                    bookLineCategory.lineSubcategory.name,
                    bookLine.money.sum().coalesce(0.0)
                )
            )
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.categories, bookLineCategory)
            .innerJoin(bookLineCategory.lineSubcategory, subcategory)
            .where(
                book.bookKey.eq(bookKey),
                bookLineCategory.lineSubcategory.in(childCategories),
                bookLine.lineDate.between(duration.getStartDate(), duration.getEndDate())
            )
            .where(
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineSubcategory.status.eq(ACTIVE),
                book.status.eq(ACTIVE)
            )
            .groupBy(bookLineCategory.lineSubcategory.name)
            .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public double totalOutcomeMoneyForBudget(final Book targetBook,
                                             final DateDuration duration) {
        return Optional.ofNullable(jpaQueryFactory.select(bookLine.money.sum().coalesce(0.0))
                .from(bookLine)
                .innerJoin(bookLine.book, book)
                .innerJoin(bookLine.categories, bookLineCategory)
                .innerJoin(bookLineCategory.lineCategory, category)
                .where(
                    book.eq(targetBook),
                    bookLine.lineDate.between(duration.getStartDate(), duration.getEndDate()),
                    bookLineCategory.lineCategory.name.eq(OUTCOME),
                    bookLine.exceptStatus.eq(false)
                )
                .where(
                    book.status.eq(ACTIVE),
                    bookLine.status.eq(ACTIVE),
                    bookLineCategory.status.eq(ACTIVE),
                    bookLineCategory.lineCategory.status.eq(ACTIVE)
                )
                .fetchOne())
            .orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> totalExpensesForAsset(final Book book, final LocalDate date) {
        final DateDuration duration = DateDuration.startAndEndOfMonth(date.toString());

        final Map<String, Double> totalExpenses = new HashMap<>();
        final double totalIncomeMoney = totalMoneyByDurationAndCategoryTypeExceptAsset(book, duration, INCOME);
        final double totalOutcomeMoney = totalMoneyByDurationAndCategoryTypeExceptAsset(book, duration, OUTCOME);

        totalExpenses.put(INCOME.getMeaning(), totalIncomeMoney);
        totalExpenses.put(OUTCOME.getMeaning(), totalOutcomeMoney);
        return totalExpenses;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookLine> findByIdWithCategoriesAndWriter(final Long id) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(bookLine)
            .innerJoin(bookLine.categories, bookLineCategory).fetchJoin()
            .innerJoin(bookLineCategory.lineCategory, category).fetchJoin()
            .innerJoin(bookLineCategory.lineSubcategory, subcategory).fetchJoin()
            .innerJoin(bookLineCategory.assetSubcategory, subcategory).fetchJoin()
            .innerJoin(bookLine.writer, bookUser).fetchJoin()
            .innerJoin(bookUser.user, user).fetchJoin()
            .where(
                bookLine.id.eq(id)
            )
            .where(
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineCategory.status.eq(ACTIVE),
                bookLineCategory.lineSubcategory.status.eq(ACTIVE),
                bookLineCategory.assetSubcategory.status.eq(ACTIVE),
                bookLineCategory.bookLine.status.eq(ACTIVE),
                bookLineCategory.bookLine.writer.status.eq(ACTIVE)
            )
            .fetchOne()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLine> findAllByBookKeyOrderByDateDesc(final String bookKey) {
        return jpaQueryFactory.selectFrom(bookLine)
            .innerJoin(bookLine.book, book).fetchJoin()
            .innerJoin(bookLine.categories, bookLineCategory).fetchJoin()
            .innerJoin(bookLineCategory.lineCategory, category).fetchJoin()
            .innerJoin(bookLineCategory.lineSubcategory, subcategory).fetchJoin()
            .innerJoin(bookLineCategory.assetSubcategory, subcategory).fetchJoin()
            .where(
                book.bookKey.eq(bookKey)
            )
            .where(
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineCategory.status.eq(ACTIVE),
                bookLineCategory.lineSubcategory.status.eq(ACTIVE),
                bookLineCategory.assetSubcategory.status.eq(ACTIVE)
            )
            .orderBy(bookLine.lineDate.desc())
            .fetch();
    }

    @Override
    public void inactiveAllByBook(final Book book) {
        jpaQueryFactory.update(bookLine)
            .set(bookLine.status, INACTIVE)
            .set(bookLine.updatedAt, LocalDateTime.now())
            .where(
                bookLine.book.eq(book),
                bookLine.status.eq(ACTIVE)
            ).execute();

        entityManager.clear();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLine> findAllByBookUser(final BookUser bookUser) {
        return jpaQueryFactory.selectFrom(bookLine)
            .where(
                bookLine.writer.eq(bookUser)
            )
            .where(
                bookLine.status.eq(ACTIVE)
            )
            .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public double totalMoneyByDurationAndCategoryType(final String bookKey,
                                                      final DateDuration duration,
                                                      final CategoryType categoryType) {
        return Optional.ofNullable(jpaQueryFactory.select(bookLine.money.sum())
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.categories, bookLineCategory)
            .innerJoin(bookLineCategory.lineCategory, category)
            .where(
                bookLine.lineDate.between(duration.getStartDate(), duration.getEndDate()),
                bookLineCategory.lineCategory.name.eq(categoryType),
                book.bookKey.eq(bookKey)
            )
            .where(
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineCategory.status.eq(ACTIVE)
            )
            .fetchOne()).orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public double incomeMoneyForAssetUntil(final Book targetBook,
                                           final YearMonth endMonth) {
        return Optional.ofNullable(jpaQueryFactory.select(bookLine.money.sum())
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.categories, bookLineCategory)
            .innerJoin(bookLineCategory.lineCategory, category)
            .where(
                bookLine.book.eq(targetBook),
                bookLineCategory.lineCategory.name.eq(INCOME),
                bookLine.lineDate.between(BookLine.START_DATE, endMonth.atEndOfMonth()),
                bookLine.exceptStatus.eq(false)
            )
            .where(
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineCategory.status.eq(ACTIVE)
            )
            .fetchOne()).orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public double outcomeMoneyUntil(final Book targetBook,
                                    final YearMonth endMonth) {
        return Optional.ofNullable(jpaQueryFactory.select(bookLine.money.sum())
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.categories, bookLineCategory)
            .innerJoin(bookLineCategory.lineCategory, category)
            .where(
                bookLine.book.eq(targetBook),
                bookLineCategory.lineCategory.name.eq(OUTCOME),
                bookLine.lineDate.between(BookLine.START_DATE, endMonth.atEndOfMonth())
            )
            .where(
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineCategory.status.eq(ACTIVE)
            )
            .fetchOne()).orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public double incomeMoneyForAssetByMonth(final Book targetBook,
                                             final YearMonth month) {
        return Optional.ofNullable(jpaQueryFactory.select(bookLine.money.sum())
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.categories, bookLineCategory)
            .innerJoin(bookLineCategory.lineCategory, category)
            .where(
                bookLine.book.eq(targetBook),
                bookLineCategory.lineCategory.name.eq(INCOME),
                bookLine.lineDate.between(month.atDay(1), month.atEndOfMonth()),
                bookLine.exceptStatus.eq(false)
            )
            .where(
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineCategory.status.eq(ACTIVE)
            )
            .fetchOne()).orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public double outcomeMoneyByMonth(final Book targetBook, final YearMonth month) {
        return Optional.ofNullable(jpaQueryFactory.select(bookLine.money.sum())
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.categories, bookLineCategory)
            .innerJoin(bookLineCategory.lineCategory, category)
            .where(
                bookLine.book.eq(targetBook),
                bookLineCategory.lineCategory.name.eq(OUTCOME),
                bookLine.lineDate.between(month.atDay(1), month.atEndOfMonth())
            )
            .where(
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineCategory.status.eq(ACTIVE)
            )
            .fetchOne()).orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLine> findAllByDurationAndLineSubcategoryAndWriters(final String bookKey,
                                                                        final DateDuration duration,
                                                                        final String lineSubcategoryName,
                                                                        final List<Long> bookUserIds) {
        return jpaQueryFactory.selectFrom(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.categories, bookLineCategory)
            .innerJoin(bookLineCategory.lineSubcategory, subcategory)
            .innerJoin(bookLine.writer, bookUser)
            .where(
                book.bookKey.eq(bookKey),
                bookLine.lineDate.between(duration.getStartDate(), duration.getEndDate()),
                bookLineCategory.lineSubcategory.name.eq(lineSubcategoryName),
                bookUser.id.in(bookUserIds),
                bookUser.book.eq(book)
            )
            .where(
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineSubcategory.status.eq(ACTIVE),
                bookUser.status.eq(ACTIVE)
            )
            .fetch();
    }

    private void validateLineSubcategory(final Subcategory subcategory) {
        subcategory.getParent().validateLine();
    }

    private double totalMoneyByDurationAndCategoryTypeExceptAsset(final Book targetBook,
                                                                  final DateDuration duration,
                                                                  final CategoryType categoryType) {
        return Optional.ofNullable(jpaQueryFactory.select(bookLine.money.sum())
            .from(bookLine)
            .innerJoin(bookLine.book, book)
            .innerJoin(bookLine.categories, bookLineCategory)
            .innerJoin(bookLineCategory.lineCategory, category)
            .where(
                bookLine.lineDate.between(duration.getStartDate(), duration.getEndDate()),
                bookLineCategory.lineCategory.name.eq(categoryType),
                book.eq(targetBook),
                bookLine.exceptStatus.eq(false)
            )
            .where(
                book.status.eq(ACTIVE),
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE),
                bookLineCategory.lineCategory.status.eq(ACTIVE)
            )
            .fetchOne()).orElse(0.0);
    }
}
