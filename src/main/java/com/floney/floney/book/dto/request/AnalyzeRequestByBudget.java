package com.floney.floney.book.dto.request;

import lombok.Getter;

import java.util.Objects;

@Getter
public class AnalyzeRequestByBudget {
    private String bookKey;

    private String date;
}
