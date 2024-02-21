package com.floney.floney.book.domain.vo;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.dto.process.BookLineExpense;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MonthKey {

    private final LocalDate date;
    private final CategoryType categoryType;

    public static MonthKey of(final LocalDate date, final CategoryType assetType) {
        return new MonthKey(date, assetType);
    }

    public static MonthKey toMonthKey(final BookLineExpense bookLineExpense) {
        return new MonthKey(bookLineExpense.getDate(), bookLineExpense.getCategoryType());
    }
}
