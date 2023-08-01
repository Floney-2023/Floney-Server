package com.floney.floney.book.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AnalyzeByCategoryRequest {

    private String bookKey;

    private String root;

    private String date;

    public LocalDate getLocalDate(){
        return LocalDate.parse(date);
    }

}
