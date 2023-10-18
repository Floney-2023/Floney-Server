package com.floney.floney.book.repository;

import static com.floney.floney.book.entity.QAlarm.alarm;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmCustomRepositoryImpl implements AlarmCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public void inactiveAllByBookUser(final BookUser bookUser) {
        jpaQueryFactory.update(alarm)
                .set(alarm.status, INACTIVE)
                .set(alarm.updatedAt, LocalDateTime.now())
                .where(
                        alarm.bookUser.eq(bookUser),
                        alarm.status.eq(ACTIVE)
                )
                .execute();
    }

    @Override
    @Transactional
    public void inactiveAllByBook(final Book book) {
        jpaQueryFactory.update(alarm)
                .set(alarm.status, INACTIVE)
                .set(alarm.updatedAt, LocalDateTime.now())
                .where(
                        alarm.book.eq(book),
                        alarm.status.eq(ACTIVE)
                )
                .execute();
    }
}
