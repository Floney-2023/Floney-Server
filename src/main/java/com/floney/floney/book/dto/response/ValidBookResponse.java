package com.floney.floney.book.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidBookResponse {
    private String bookKey;

    private ValidBookResponse(String bookKey) {
        this.bookKey = bookKey;
    }

    public static ValidBookResponse of(String recentBookKey) {
        return new ValidBookResponse(recentBookKey);
    }
}
