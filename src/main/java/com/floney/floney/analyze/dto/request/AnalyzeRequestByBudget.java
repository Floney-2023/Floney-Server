package com.floney.floney.analyze.dto.request;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class AnalyzeRequestByBudget {
    private String bookKey;

    private String date;
}
