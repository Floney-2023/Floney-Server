package com.floney.floney.book.repository;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.dto.response.BudgetYearResponse;
import com.floney.floney.book.dto.response.QBudgetYearResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.floney.floney.book.domain.entity.QBook.book;
import static com.floney.floney.book.domain.entity.QBookUser.bookUser;
import static com.floney.floney.book.domain.entity.QBudget.budget;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.user.entity.QUser.user;

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
                bookUser.status.eq(ACTIVE),
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

    @Override
    public List<Book> findAllByUserEmail(final String userEmail) {
        return jpaQueryFactory.selectFrom(book)
                .innerJoin(book, bookUser.book)
                .innerJoin(bookUser.user, user)
                .where(
                        user.email.eq(userEmail),
                        book.status.eq(ACTIVE),
                        bookUser.status.eq(ACTIVE)
                )
                .fetch();
    }
}
