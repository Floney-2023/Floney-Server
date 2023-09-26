package com.floney.floney.user.repository;

import static com.floney.floney.user.entity.QSignoutReason.signoutReason;

import com.floney.floney.user.dto.constant.SignoutType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignoutReasonCustomRepositoryImpl implements SignoutReasonCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public void increaseCount(final SignoutType signoutType) {
        jpaQueryFactory.update(signoutReason)
                .set(signoutReason.count, signoutReason.count.add(1))
                .where(signoutReason.reasonType.eq(signoutType))
                .execute();
    }
}
