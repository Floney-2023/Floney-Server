package com.floney.floney.book.dto.process;

import com.floney.floney.book.domain.category.CategoryType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BookLineExpense {

    private final LocalDate date;
    private final double money;
    private final CategoryType categoryType;

    @Builder
    @QueryProjection
    public BookLineExpense(final LocalDate date, final double money, final CategoryType categoryType) {
        this.date = date;
        this.money = money;
        this.categoryType = categoryType;
    }

    public static BookLineExpense of(final LocalDate targetDate, final CategoryType type) {
        return new BookLineExpense(targetDate, 0.0, type);
    }
}
