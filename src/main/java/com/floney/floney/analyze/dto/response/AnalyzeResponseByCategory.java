package com.floney.floney.analyze.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class AnalyzeResponseByCategory {

    private final String category;
    private final double money;

    @QueryProjection
    public AnalyzeResponseByCategory(final String category, final double money) {
        this.category = category;
        this.money = money;
    }
}
