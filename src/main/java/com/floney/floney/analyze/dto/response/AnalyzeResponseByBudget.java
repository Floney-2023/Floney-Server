package com.floney.floney.analyze.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class AnalyzeResponseByBudget {
    private final long leftMoney;
    private final long totalMoney;

    @QueryProjection
    public AnalyzeResponseByBudget(long totalMoney, long initMoney) {
        this.totalMoney = totalMoney;
        this.leftMoney = calculateLeftMoney(totalMoney, initMoney);
    }

    private long calculateLeftMoney(long totalMoney, long initMoney) {
        return initMoney - totalMoney;
    }
}
