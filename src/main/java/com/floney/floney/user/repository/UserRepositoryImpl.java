package com.floney.floney.user.repository;

import com.floney.floney.book.util.DateFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void deleteUserAfterMonth() {
        jpaQueryFactory
            .delete(user)
            .where(user.deleteTime.before(DateFactory.getThreeMonthAgo()),
                user.status.eq(INACTIVE))
            .execute();
    }
}
