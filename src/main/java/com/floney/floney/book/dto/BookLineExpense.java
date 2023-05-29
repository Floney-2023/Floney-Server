package com.floney.floney.book.dto;

import com.floney.floney.book.dto.constant.AssetType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@EqualsAndHashCode
public class BookLineExpense {
    private final LocalDate date;
    private final Long money;
    private final AssetType assetType;


    public BookLineExpense(LocalDate date, AssetType assetType) {
        this.date = date;
        this.money = 0L;
        this.assetType = assetType;
    }

    @QueryProjection
    @Builder
    public BookLineExpense(LocalDate date, Long money, String assetType) {
        this.date = date;
        this.money = money;
        this.assetType = AssetType.find(assetType);
    }

    public static BookLineExpense initExpense(LocalDate targetDate, AssetType type) {
        return new BookLineExpense(targetDate,type);
    }


}
