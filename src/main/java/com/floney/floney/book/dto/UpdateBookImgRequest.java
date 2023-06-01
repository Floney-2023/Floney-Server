package com.floney.floney.book.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UpdateBookImgRequest {
    private String newUrl;
    private String bookKey;

    @Builder
    private UpdateBookImgRequest(String newUrl, String bookKey) {
        this.newUrl = newUrl;
        this.bookKey = bookKey;
    }
}
