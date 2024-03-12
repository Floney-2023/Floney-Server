package com.floney.floney.analyze.dto.request;

import lombok.*;

import java.time.YearMonth;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyzeRequestByAsset {

    private String bookKey;
    private YearMonth date;
}
