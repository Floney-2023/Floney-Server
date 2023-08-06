package com.floney.floney.book.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class AnalyzeResponseByBudget {
    private final Long leftMoney;
    private final Long totalMoney;

    @QueryProjection
    public AnalyzeResponseByBudget(Long totalMoney, Long initMoney) {
        this.totalMoney = totalMoney;
        this.leftMoney = calculateLeftMoney(totalMoney, initMoney);
    }

    private Long calculateLeftMoney(Long totalMoney, Long initMoney) {
        return initMoney - totalMoney;
    }
}
