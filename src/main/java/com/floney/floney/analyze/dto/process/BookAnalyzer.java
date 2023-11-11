package com.floney.floney.analyze.dto.process;

import com.floney.floney.analyze.dto.response.AnalyzeResponseByAsset;
import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.book.dto.process.AssetInfo;

import java.time.LocalDate;
import java.util.Map;

public class BookAnalyzer {
    private static final int STANDARD = 0;
    private final Map<String, Float> totalExpenses;

    public BookAnalyzer(Map<String, Float> totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public float differenceInAndOutCome() {
        return totalExpenses.get(AssetType.INCOME.getKind()) - totalExpenses.get(AssetType.OUTCOME.getKind());
    }

    public float calculateCurrentAsset(float initAsset, float difference) {
        if (difference < STANDARD) {
            return initAsset - Math.abs(difference);
        }
        return initAsset + difference;
    }

    public AnalyzeResponseByAsset analyzeAsset(float savedAsset, Map<LocalDate, AssetInfo> assetInfo) {
        float difference = differenceInAndOutCome();
        float currentAsset = calculateCurrentAsset(savedAsset, difference);

        return AnalyzeResponseByAsset.of(difference, savedAsset, currentAsset, assetInfo);
    }
}
