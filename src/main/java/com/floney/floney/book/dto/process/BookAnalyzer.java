package com.floney.floney.book.dto.process;

import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.book.dto.response.AnalyzeResponseByAsset;

import java.util.Map;

public class BookAnalyzer {
    private final Map<String, Long> totalExpenses;

    public BookAnalyzer(Map<String, Long> totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public long differenceInAndOutCome() {
        return totalExpenses.get(AssetType.INCOME.getKind()) - totalExpenses.get(AssetType.OUTCOME.getKind());
    }

    public long calculateCurrentAsset(long initAsset, long difference) {
        if (difference < 0) {
            return initAsset - Math.abs(difference);
        }
        return initAsset + difference;
    }

    public AnalyzeResponseByAsset analyzeAsset(long initAsset) {
        long difference = differenceInAndOutCome();
        long currentAsset = calculateCurrentAsset(initAsset, difference);

        return AnalyzeResponseByAsset.of(difference, initAsset, currentAsset);
    }
}
