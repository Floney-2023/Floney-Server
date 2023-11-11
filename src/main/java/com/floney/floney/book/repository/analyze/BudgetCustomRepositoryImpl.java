package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.entity.Book;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.floney.floney.book.entity.QBudget.budget;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BudgetCustomRepositoryImpl implements BudgetCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public void inactiveAllByBook(final Book book) {
        jpaQueryFactory.update(budget)
                .set(budget.status, INACTIVE)
                .set(budget.updatedAt, LocalDateTime.now())
                .where(
                        budget.book.eq(book),
                        budget.status.eq(ACTIVE)
                )
                .execute();
    }
}
