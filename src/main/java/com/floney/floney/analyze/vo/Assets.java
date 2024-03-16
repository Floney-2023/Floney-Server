package com.floney.floney.analyze.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.YearMonth;
import java.util.SortedMap;
import java.util.TreeMap;

@Getter
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Assets {

    public static final int MONTHS = 6;

    private final SortedMap<YearMonth, Double> values;

    public static Assets create(final double initialAsset, final YearMonth endMonth) {
        final SortedMap<YearMonth, Double> values = new TreeMap<>();
        for (int month = 0; month < MONTHS; month++) {
            values.put(endMonth.minusMonths(month), initialAsset);
        }
        return new Assets(values);
    }

    public void update(final YearMonth currentMonth, final double asset) {
        if (!values.containsKey(currentMonth)) {
            log.error("Assets 로직 에러: {} 날짜에 해당하는 자산이 없음. 총 자산: {}", currentMonth, values);
            throw new IllegalArgumentException("Assets 에서 잘못된 달(" + currentMonth + ")의 자산을 가져옴");
        }
        values.put(currentMonth, values.get(currentMonth) + asset);
    }

    public double getLastAsset() {
        return values.get(values.lastKey());
    }
}
