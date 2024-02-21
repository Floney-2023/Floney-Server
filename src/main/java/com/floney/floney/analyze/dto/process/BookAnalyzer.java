package com.floney.floney.analyze.dto.process;

import com.floney.floney.analyze.dto.response.AnalyzeResponseByAsset;
import com.floney.floney.book.dto.process.AssetInfo;

import java.time.LocalDate;
import java.util.Map;

import static com.floney.floney.book.domain.category.CategoryType.INCOME;
import static com.floney.floney.book.domain.category.CategoryType.OUTCOME;

public class BookAnalyzer {

    private static final int STANDARD = 0;

    private final Map<String, Double> totalExpenses;

    public BookAnalyzer(Map<String, Double> totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public double differenceInAndOutCome() {
        return totalExpenses.get(INCOME.getMeaning()) - totalExpenses.get(OUTCOME.getMeaning());
    }

    public AnalyzeResponseByAsset analyzeAsset(final String requestMonth,
                                               final double savedAsset,
                                               final Map<LocalDate, AssetInfo> assetInfo) {
        double difference = differenceInAndOutCome();
        LocalDate requestDate = LocalDate.parse(requestMonth);
        double currentAsset = assetInfo.get(requestDate).getAssetMoney();

        return AnalyzeResponseByAsset.of(difference, savedAsset, currentAsset, assetInfo);
    }
}
