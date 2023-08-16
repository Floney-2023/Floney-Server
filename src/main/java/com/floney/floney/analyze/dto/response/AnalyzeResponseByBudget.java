package com.floney.floney.analyze.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class AnalyzeResponseByBudget {
    private final long leftMoney;
    private final long initMoney;

    @QueryProjection
    public AnalyzeResponseByBudget(long totalOutcomeMoney, long initMoney) {
        this.initMoney = initMoney;
        this.leftMoney = calculateLeftMoney(initMoney, totalOutcomeMoney);
    }

    private long calculateLeftMoney(long initMoney, long totalOutcomeMoney) {
        return initMoney - totalOutcomeMoney;
    }
}
