package com.floney.floney.analyze.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AnalyzeRequestByAsset {
    private String bookKey;

    private String date;
}
