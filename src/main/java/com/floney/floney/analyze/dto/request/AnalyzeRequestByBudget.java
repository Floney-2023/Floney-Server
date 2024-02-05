package com.floney.floney.analyze.dto.request;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyzeRequestByBudget {

    // TODO: validation 추가
    private String bookKey;
    private String date;
}
