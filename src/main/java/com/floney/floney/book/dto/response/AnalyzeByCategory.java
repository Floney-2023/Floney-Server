package com.floney.floney.book.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AnalyzeByCategory {
    private String category;
    private Long money;
    @QueryProjection
    public AnalyzeByCategory(String category, Long money) {
        this.category = category;
        this.money = money;
    }

}
