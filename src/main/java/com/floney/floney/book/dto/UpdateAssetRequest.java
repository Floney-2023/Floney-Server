package com.floney.floney.book.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateAssetRequest {
    private String bookKey;
    private Long asset;

    public UpdateAssetRequest(String bookKey, Long asset) {
        this.asset = asset;
        this.bookKey = bookKey;
    }
}
