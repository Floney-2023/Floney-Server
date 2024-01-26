package com.floney.floney.book.dto.process;

import com.floney.floney.book.domain.constant.AssetType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class MonthKey {
    private LocalDate date;
    private AssetType assetType;

    public static MonthKey of(LocalDate date, AssetType assetType) {
        return new MonthKey(date, assetType);
    }

    public static MonthKey toMonthKey(BookLineExpense expense) {
        return new MonthKey(expense.getDate(), expense.getAssetType());
    }

}
