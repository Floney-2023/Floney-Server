package com.floney.floney.book.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class SaveRecentBookKeyRequest {

    private String bookKey;

    public SaveRecentBookKeyRequest(String bookKey) {
        this.bookKey = bookKey;
    }
}
