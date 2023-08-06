package com.floney.floney.book.dto.response;

import com.floney.floney.book.dto.constant.AssetType;
import lombok.Getter;

import java.util.Map;

@Getter
public class AnalyzeResponseByAsset {
    private final Long difference;
    private final Long initAsset;
    private final Long currentAsset;

    private AnalyzeResponseByAsset(Map<String, Long> totalExpenses, Long initAsset) {
        this.difference = differenceInAndOut(totalExpenses);
        this.currentAsset = calculateCurrentAsset(initAsset, difference);
        this.initAsset = initAsset;
    }

    public static AnalyzeResponseByAsset of(Map<String, Long> totalExpenses, Long initAsset) {
        return new AnalyzeResponseByAsset(totalExpenses, initAsset);
    }

    private Long differenceInAndOut(Map<String, Long> totalExpenses) {
        return totalExpenses.get(AssetType.INCOME.getKind()) - totalExpenses.get(AssetType.OUTCOME.getKind());
    }

    private Long calculateCurrentAsset(Long initAsset, Long difference) {
        if (difference < 0) {
            return initAsset - Math.abs(difference);
        }
        return initAsset + difference;
    }
}
