package com.floney.floney.analyze.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AnalyzeResponseByCategory {
    private String category;
    private long money;

    @QueryProjection
    public AnalyzeResponseByCategory(String category, long money) {
        this.category = category;
        this.money = money;
    }

}
