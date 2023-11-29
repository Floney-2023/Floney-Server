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

    public AnalyzeResponseByAsset analyzeAsset(String requestMonth,double savedAsset, Map<LocalDate, AssetInfo> assetInfo) {
        double difference = differenceInAndOutCome();
        LocalDate requestDate = LocalDate.parse(requestMonth);
        double currentAsset = assetInfo.get(requestDate).getAssetMoney();

        return AnalyzeResponseByAsset.of(difference, savedAsset, currentAsset, assetInfo);
    }
}
