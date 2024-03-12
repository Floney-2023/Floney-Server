package com.floney.floney.analyze.dto.response;

import com.floney.floney.book.dto.process.AssetInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyzeResponseByAsset {

    private final double difference;
    private final double initAsset;
    private final double currentAsset;
    private final Map<LocalDate, AssetInfo> assetInfo;

    public static AnalyzeResponseByAsset of(double difference, double initAsset, double currentAsset, Map<LocalDate, AssetInfo> assetInfo) {
        return new AnalyzeResponseByAsset(difference, initAsset, currentAsset, assetInfo);
    }
}
