package com.floney.floney.analyze.dto.response;

import lombok.Getter;

@Getter
public class AnalyzeResponseByAsset {
    private final double difference;
    private final double initAsset;
    private final double currentAsset;

    private AnalyzeResponseByAsset(double difference, double initAsset, double currentAsset) {
        this.difference = difference;
        this.currentAsset = currentAsset;
        this.initAsset = initAsset;
    }

    public static AnalyzeResponseByAsset of(double difference, double initAsset, double currentAsset) {
        return new AnalyzeResponseByAsset(difference, initAsset, currentAsset);
    }
}
