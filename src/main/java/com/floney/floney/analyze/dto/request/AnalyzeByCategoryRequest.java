package com.floney.floney.analyze.dto.request;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class AnalyzeByCategoryRequest {

    private String bookKey;

    private String root;

    private String date;

    public LocalDate getLocalDate() {
        return LocalDate.parse(date);
    }

}
