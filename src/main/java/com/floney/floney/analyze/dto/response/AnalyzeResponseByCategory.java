package com.floney.floney.analyze.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AnalyzeResponseByCategory {
    private String category;
    private float money;

    @QueryProjection
    public AnalyzeResponseByCategory(String category, float money) {
        this.category = category;
        this.money = money;
    }

}
