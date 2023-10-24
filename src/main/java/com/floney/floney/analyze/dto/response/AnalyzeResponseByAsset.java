package com.floney.floney.analyze.dto.response;

import lombok.Getter;

@Getter
public class AnalyzeResponseByAsset {
    private final long difference;
    private final long initAsset;
    private final long currentAsset;

    private AnalyzeResponseByAsset(long difference, long initAsset, long currentAsset) {
        this.difference = difference;
        this.currentAsset = currentAsset;
        this.initAsset = initAsset;
    }

    public static AnalyzeResponseByAsset of(long difference, long initAsset, long currentAsset) {
        return new AnalyzeResponseByAsset(difference, initAsset, currentAsset);
    }
}
