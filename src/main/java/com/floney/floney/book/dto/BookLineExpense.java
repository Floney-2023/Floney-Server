package com.floney.floney.book.dto;

import com.floney.floney.book.entity.AssetType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Objects;

public class BookLineExpense {
    private LocalDate date;
    private Long money;
    private AssetType assetType;

    @QueryProjection
    @Builder
    public BookLineExpense(LocalDate date, Long money, String assetType) {
        this.date = date;
        this.money = money;
        this.assetType = AssetType.find(assetType);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookLineExpense that = (BookLineExpense) o;
        return Objects.equals(date, that.date) && Objects.equals(money, that.money) && assetType == that.assetType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, money, assetType);
    }
}
