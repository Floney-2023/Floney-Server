package com.floney.floney.book.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class AllSettlementsRequest {
    private List<String> usersEmails;

    private LocalDate startDate;

    private LocalDate endDate;

    private DatesRequest dates;

    public AllSettlementsRequest(List<String> usersEmails, LocalDate startDate, LocalDate endDate) {
        this.usersEmails = usersEmails;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public List<String> users() {
        return usersEmails;
    }

    public void datesTo() {
        this.dates = DatesRequest.builder()
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }
}
