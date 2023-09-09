package com.floney.floney.common.dto;

import lombok.Getter;

@Getter
public class DelegateResponse {
    private final boolean isDelegate;
    private final String bookName;

    public DelegateResponse(boolean isDelegate, String bookName) {
        this.isDelegate = isDelegate;
        this.bookName = bookName;
    }
}
