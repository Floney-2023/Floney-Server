package com.floney.floney.book.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvolveBookResponse {
    private String bookKey;

    private InvolveBookResponse(String bookKey) {
        this.bookKey = bookKey;
    }

    public static InvolveBookResponse of(String recentBookKey) {
        return new InvolveBookResponse(recentBookKey);
    }
}
