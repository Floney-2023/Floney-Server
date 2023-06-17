package com.floney.floney.book.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateBudgetRequest {
    private String bookKey;
    private Long budget;

    public UpdateBudgetRequest(String bookKey, Long budget) {
        this.budget = budget;
        this.bookKey = bookKey;
    }
}
