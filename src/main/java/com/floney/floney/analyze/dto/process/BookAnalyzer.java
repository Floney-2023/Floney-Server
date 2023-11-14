package com.floney.floney.analyze.dto.process;

import com.floney.floney.analyze.dto.response.AnalyzeResponseByAsset;
import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.book.dto.process.AssetInfo;

import java.time.LocalDate;
import java.util.Map;

public class BookAnalyzer {
    private static final int STANDARD = 0;
    private final Map<String, Double> totalExpenses;

    public BookAnalyzer(Map<String, Double> totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public double differenceInAndOutCome() {
        return totalExpenses.get(AssetType.INCOME.getKind()) - totalExpenses.get(AssetType.OUTCOME.getKind());
    }

    public double calculateCurrentAsset(double initAsset, double difference) {
        if (difference < STANDARD) {
            return initAsset - Math.abs(difference);
        }
        return initAsset + difference;
    }

    public AnalyzeResponseByAsset analyzeAsset(double savedAsset, Map<LocalDate, AssetInfo> assetInfo) {
        double difference = differenceInAndOutCome();
        double currentAsset = calculateCurrentAsset(savedAsset, difference);

        return AnalyzeResponseByAsset.of(difference, savedAsset, currentAsset, assetInfo);
    }
}
