package com.floney.floney.settlement.repository;

import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.settlement.domain.entity.QSettlement.settlement;

import com.floney.floney.settlement.domain.entity.Settlement;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementRepositoryImpl implements SettlementCustomRepository {
    private final static int DELETE_TERM = 3;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Settlement> findSettlementHaveToDelete() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(DELETE_TERM);
        return jpaQueryFactory
            .selectFrom(settlement)
            .where(settlement.updatedAt.before(threeMonthsAgo),
                settlement.status.eq(INACTIVE))
            .fetch();
    }
}
