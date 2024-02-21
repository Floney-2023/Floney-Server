package com.floney.floney.book.dto.process;

import com.floney.floney.book.domain.category.CategoryType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TotalExpense {

    private final CategoryType categoryType;
    private final double money;

    @Builder
    @QueryProjection
    public TotalExpense(final CategoryType categoryType, final double money) {
        this.money = money;
        this.categoryType = categoryType;
    }
}
