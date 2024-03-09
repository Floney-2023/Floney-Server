package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Book;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static com.floney.floney.book.domain.entity.QCarryOver.carryOver;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CarryOverCustomRepositoryImpl implements CarryOverCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void inactiveAllBy(final Book book) {
        // unique index 가 걸려서 INACTIVE 데이터 먼저 삭제
        jpaQueryFactory.delete(carryOver)
            .where(
                carryOver.book.eq(book),
                carryOver.status.eq(INACTIVE)
            )
            .execute();

        jpaQueryFactory.update(carryOver)
            .set(carryOver.status, INACTIVE)
            .set(carryOver.updatedAt, LocalDateTime.now())
            .where(
                carryOver.book.eq(book),
                carryOver.status.eq(ACTIVE)
            )
            .execute();

        entityManager.clear();
    }

    @Override
    @Transactional
    public void inactiveAllByBook(final Book book) {
        jpaQueryFactory.update(carryOver)
            .set(carryOver.status, INACTIVE)
            .set(carryOver.updatedAt, LocalDateTime.now())
            .where(
                carryOver.book.eq(book),
                carryOver.status.eq(ACTIVE)
            )
            .execute();

        entityManager.clear();
    }
}
