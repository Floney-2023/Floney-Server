package com.floney.floney.book.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class DatesRequest {
    private LocalDate startDate;

    private LocalDate endDate;

    @Builder
    public DatesRequest(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate start(){
        return startDate;
    }

    public LocalDate end(){
        return endDate;
    }
}
