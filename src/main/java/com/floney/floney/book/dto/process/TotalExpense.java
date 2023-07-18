package com.floney.floney.book.dto.process;

import com.floney.floney.book.dto.constant.AssetType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TotalExpense {

    private final Long money;
    private final AssetType assetType;

    @QueryProjection
    @Builder
    public TotalExpense(Long money, String assetType) {
        this.money = money;
        this.assetType = AssetType.find(assetType);
    }
}
