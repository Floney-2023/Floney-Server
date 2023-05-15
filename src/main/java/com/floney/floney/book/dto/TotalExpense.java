package com.floney.floney.book.dto;

import com.floney.floney.book.dto.constant.AssetType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TotalExpense {

    private Long money;
    private AssetType assetType;

    @QueryProjection
    @Builder
    public TotalExpense(Long money, String assetType) {
        this.money = money;
        this.assetType = AssetType.find(assetType);
    }
}