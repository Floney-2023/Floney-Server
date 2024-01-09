package com.floney.floney.book.dto.process;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
@ToString
public class DatesDuration {
    private LocalDate startDate;

    private LocalDate endDate;

    @Builder
    private DatesDuration(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate start() {
        return startDate;
    }

    public LocalDate end() {
        return endDate;
    }
}
