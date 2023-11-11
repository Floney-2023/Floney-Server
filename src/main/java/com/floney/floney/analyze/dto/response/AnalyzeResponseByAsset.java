package com.floney.floney.analyze.dto.response;

import com.floney.floney.book.dto.process.AssetInfo;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
public class AnalyzeResponseByAsset {
    private final float difference;
    private final float initAsset;
    private final float currentAsset;
    private final Map<LocalDate, AssetInfo> assetInfo;

    private AnalyzeResponseByAsset(float difference, float initAsset, float currentAsset, Map<LocalDate, AssetInfo> assetInfo) {
        this.difference = difference;
        this.currentAsset = currentAsset;
        this.initAsset = initAsset;
        this.assetInfo = assetInfo;
    }

    public static AnalyzeResponseByAsset of(float difference, float initAsset, float currentAsset, Map<LocalDate,AssetInfo> assetInfo) {
        return new AnalyzeResponseByAsset(difference, initAsset, currentAsset, assetInfo);
    }
}
