package com.floney.floney.book.dto.response;

import com.floney.floney.book.dto.constant.AssetType;
import lombok.Getter;

import java.util.Map;

@Getter
public class AnalyzeResponseByAsset {
    private final long difference;
    private final long initAsset;
    private final long currentAsset;

    private AnalyzeResponseByAsset(long difference, long initAsset,long currentAsset) {
        this.difference = difference;
        this.currentAsset = currentAsset;
        this.initAsset = initAsset;
    }

    public static AnalyzeResponseByAsset of(long difference, long initAsset,long currentAsset) {
        return new AnalyzeResponseByAsset(difference, initAsset,currentAsset);
    }




}
