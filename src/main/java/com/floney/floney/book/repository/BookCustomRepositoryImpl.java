package com.floney.floney.book.repository;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookUser.bookUser;
import static com.floney.floney.book.entity.QBudget.budget;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.user.entity.QUser.user;

import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.dto.response.BudgetYearResponse;
import com.floney.floney.book.dto.response.QBudgetYearResponse;
import com.floney.floney.book.entity.Book;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookCustomRepositoryImpl implements BookCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Book> findByBookUserEmailAndBookKey(final String userEmail, final String bookKey) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(bookUser)
                .innerJoin(bookUser.book, book).fetchJoin()
                .innerJoin(bookUser.user, user).fetchJoin()
                .where(
                        user.email.eq(userEmail), book.bookKey.eq(bookKey),
                        book.status.eq(ACTIVE), user.status.eq(ACTIVE)
                )
                .fetchOne().getBook());
    }

    @Override
    public List<BudgetYearResponse> findBudgetByYear(String bookKey, DatesDuration duration) {
        return jpaQueryFactory
                .select(new QBudgetYearResponse(budget.date, budget.money))
                .from(budget)
                .innerJoin(budget.book, book)
                .where(book.bookKey.eq(bookKey))
                .where(budget.date.between(duration.getStartDate(), duration.getEndDate()))
                .fetch();
    }
}
