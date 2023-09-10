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
    private Long asset;
    private LocalDate date;

    public UpdateAssetRequest(String bookKey, Long asset, LocalDate date) {
        this.asset = asset;
        this.bookKey = bookKey;
        this.date = date;
    }
}
