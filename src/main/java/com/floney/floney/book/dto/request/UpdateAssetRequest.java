package com.floney.floney.book.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@RequiredArgsConstructor
public class UpdateAssetRequest {
    private String bookKey;
    private double asset;

    public UpdateAssetRequest(String bookKey, Double asset, LocalDate date) {
        this.asset = asset;
        this.bookKey = bookKey;
    }
}
