package com.floney.floney.book.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@RequiredArgsConstructor
public class UpdateBudgetRequest {
    private String bookKey;
    private LocalDate date;
    private Long budget;

    public UpdateBudgetRequest(String bookKey, LocalDate date, Long budget) {
        this.date = date;
        this.budget = budget;
        this.bookKey = bookKey;
    }
}
