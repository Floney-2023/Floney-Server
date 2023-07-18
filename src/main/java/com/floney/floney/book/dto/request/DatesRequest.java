package com.floney.floney.book.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
public class DatesRequest {
    private LocalDate startDate;

    private LocalDate endDate;

    @Builder
    private DatesRequest(LocalDate startDate, LocalDate endDate) {
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
