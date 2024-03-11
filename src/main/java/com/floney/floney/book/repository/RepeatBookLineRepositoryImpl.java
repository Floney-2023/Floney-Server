package com.floney.floney.book.repository;


import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static com.floney.floney.book.domain.entity.QBookUser.bookUser;
import static com.floney.floney.book.domain.entity.QRepeatBookLine.repeatBookLine;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

@Repository
@Transactional
@RequiredArgsConstructor
public class RepeatBookLineRepositoryImpl implements RepeatBookLineCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void inactiveAllByBook(final Book book) {
        jpaQueryFactory.update(repeatBookLine)
            .set(repeatBookLine.status, INACTIVE)
            .set(repeatBookLine.updatedAt, LocalDateTime.now())
            .where(
                repeatBookLine.book.eq(book),
                repeatBookLine.status.eq(ACTIVE)
            )
            .execute();

        entityManager.clear();
    }

    @Override
    @Transactional
    public void inactiveAllByBookUser(final BookUser targetBookUser) {
        final JPQLQuery<Long> repeatBookLineIdByBookUser =
            JPAExpressions.select(repeatBookLine.id)
                .from(repeatBookLine)
                .innerJoin(repeatBookLine.writer, bookUser)
                .where(
                    bookUser.eq(targetBookUser),
                    repeatBookLine.status.eq(ACTIVE)
                );

        jpaQueryFactory.update(repeatBookLine)
            .set(repeatBookLine.status, INACTIVE)
            .set(repeatBookLine.updatedAt, LocalDateTime.now())
            .where(
                repeatBookLine.id.in(repeatBookLineIdByBookUser),
                repeatBookLine.status.eq(ACTIVE)
            )
            .execute();

        entityManager.clear();
    }

}
