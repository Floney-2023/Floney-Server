package com.floney.floney.analyze.dto.response;

import com.floney.floney.book.dto.process.AssetInfo;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
public class AnalyzeResponseByAsset {
    private final double difference;
    private final double initAsset;
    private final double currentAsset;
    private final Map<LocalDate, AssetInfo> assetInfo;

    private AnalyzeResponseByAsset(double difference, double initAsset, double currentAsset, Map<LocalDate, AssetInfo> assetInfo) {
        this.difference = difference;
        this.currentAsset = currentAsset;
        this.initAsset = initAsset;
        this.assetInfo = assetInfo;
    }

    public static AnalyzeResponseByAsset of(double difference, double initAsset, double currentAsset, Map<LocalDate,AssetInfo> assetInfo) {
        return new AnalyzeResponseByAsset(difference, initAsset, currentAsset, assetInfo);
    }
}
