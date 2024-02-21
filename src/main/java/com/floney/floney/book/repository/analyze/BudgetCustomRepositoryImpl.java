package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.dto.response.BudgetYearResponse;
import com.floney.floney.book.dto.response.QBudgetYearResponse;
import com.floney.floney.common.domain.vo.DateDuration;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static com.floney.floney.book.domain.entity.QBook.book;
import static com.floney.floney.book.domain.entity.QBudget.budget;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BudgetCustomRepositoryImpl implements BudgetCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void inactiveAllBy(final Book book) {
        jpaQueryFactory.update(budget)
            .set(budget.status, INACTIVE)
            .set(budget.updatedAt, LocalDateTime.now())
            .where(
                budget.book.eq(book),
                budget.status.eq(ACTIVE)
            )
            .execute();

        entityManager.clear();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetYearResponse> findBudgetByYear(final String bookKey,
                                                     final DateDuration duration) {
        return jpaQueryFactory.select(new QBudgetYearResponse(budget.date, budget.money))
            .from(budget)
            .innerJoin(budget.book, book)
            .where(
                book.bookKey.eq(bookKey),
                budget.date.between(duration.getStartDate(), duration.getEndDate())
            )
            .fetch();
    }
}
