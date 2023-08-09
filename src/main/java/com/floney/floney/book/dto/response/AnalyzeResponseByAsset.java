package com.floney.floney.book.dto.response;

import com.floney.floney.book.dto.constant.AssetType;
import lombok.Getter;

import java.util.Map;

@Getter
public class AnalyzeResponseByAsset {
    private final long difference;
    private final long initAsset;
    private final long currentAsset;

    private AnalyzeResponseByAsset(Map<String, Long> totalExpenses, long initAsset) {
        this.difference = differenceInAndOut(totalExpenses);
        this.currentAsset = calculateCurrentAsset(initAsset, difference);
        this.initAsset = initAsset;
    }

    public static AnalyzeResponseByAsset of(Map<String, Long> totalExpenses, long initAsset) {
        return new AnalyzeResponseByAsset(totalExpenses, initAsset);
    }

    private long differenceInAndOut(Map<String, Long> totalExpenses) {
        return totalExpenses.get(AssetType.INCOME.getKind()) - totalExpenses.get(AssetType.OUTCOME.getKind());
    }

    private long calculateCurrentAsset(long initAsset, long difference) {
        if (difference < 0) {
            return initAsset - Math.abs(difference);
        }
        return initAsset + difference;
    }
}
