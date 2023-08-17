package com.floney.floney.book.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SaveRecentBookKeyRequest {

    private String bookKey;

    public SaveRecentBookKeyRequest(String bookKey) {
        this.bookKey = bookKey;
    }
}
