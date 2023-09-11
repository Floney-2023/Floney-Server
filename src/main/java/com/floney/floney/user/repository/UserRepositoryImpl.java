package com.floney.floney.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository {
    private static final int DELETE_TERM = 3;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void deleteUserAfterMonth() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(DELETE_TERM);
        jpaQueryFactory
            .delete(user)
            .where(user.deleteTime.before(threeMonthsAgo),
                user.status.eq(INACTIVE))
            .execute();
    }
}
