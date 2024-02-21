package com.floney.floney.analyze.dto.request;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyzeRequestByAsset {

    private String bookKey;
    private String date;
}
