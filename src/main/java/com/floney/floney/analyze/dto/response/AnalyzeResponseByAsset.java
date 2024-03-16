package com.floney.floney.analyze.dto.response;

import com.floney.floney.analyze.vo.Assets;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.YearMonth;
import java.util.SortedMap;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyzeResponseByAsset {

    private final double difference;
    private final double initAsset;
    private final double currentAsset;
    private final SortedMap<YearMonth, Double> assetInfo;

    public static AnalyzeResponseByAsset of(final double difference,
                                            final double initAsset,
                                            final Assets assets) {
        return new AnalyzeResponseByAsset(difference, initAsset, assets.getLastAsset(), assets.getValues());
    }
}
