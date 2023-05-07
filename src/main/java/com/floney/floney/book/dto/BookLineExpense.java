package com.floney.floney.book.dto;

import com.floney.floney.book.entity.AssetType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
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


}
