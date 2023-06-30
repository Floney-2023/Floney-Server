package com.floney.floney.book.dto;

import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.book.entity.BookLine;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CarryOverInfo {
    private AssetType assetType;
    private BookLine bookLine;

    @Builder
    public CarryOverInfo(AssetType assetType, BookLine bookLine) {
        this.assetType = assetType;
        this.bookLine = bookLine;
    }
}
