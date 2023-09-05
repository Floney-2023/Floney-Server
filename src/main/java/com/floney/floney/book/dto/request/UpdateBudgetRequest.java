package com.floney.floney.book.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class UpdateBudgetRequest {
    private String bookKey;
    private Long budget;

    public UpdateBudgetRequest(String bookKey, Long budget) {
        this.budget = budget;
        this.bookKey = bookKey;
    }
}
