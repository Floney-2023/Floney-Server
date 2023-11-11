package com.floney.floney.analyze.dto.response;

import lombok.Getter;

@Getter
public class AnalyzeResponseByBudget {
    private final float leftMoney;
    private final float initBudget;

    private AnalyzeResponseByBudget(float totalOutcomeMoney, float initBudget) {
        this.initBudget = initBudget;
        this.leftMoney = calculateLeftMoney(initBudget, totalOutcomeMoney);
    }

    public static AnalyzeResponseByBudget of(float totalOutcome, float initBudget) {
        return new AnalyzeResponseByBudget(totalOutcome, initBudget);
    }

    private float calculateLeftMoney(float initBudget, float totalOutcomeMoney) {
        return initBudget - totalOutcomeMoney;
    }
}
