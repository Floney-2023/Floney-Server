package com.floney.floney.book.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class UpdateAssetRequest {
    private String bookKey;
    private Long asset;

    public UpdateAssetRequest(String bookKey, Long asset) {
        this.asset = asset;
        this.bookKey = bookKey;
    }
}
