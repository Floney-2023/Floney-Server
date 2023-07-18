package com.floney.floney.book.util;

import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.request.DatesRequest;
import com.floney.floney.book.dto.process.MonthKey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.floney.floney.book.dto.constant.AssetType.INCOME;
import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;

public class DateFactory {
    public static final int NEXT_DAY = 1;

    public static DatesRequest getDate(String targetDate) {
        LocalDate startDate = LocalDate.parse(targetDate, DateTimeFormatter.ISO_DATE);
        YearMonth yearMonth = YearMonth.from(startDate);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return DatesRequest.builder()
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

    public static Map<MonthKey, BookLineExpense> initDates(String targetDate) {
        DatesRequest dates = getDate(targetDate);
        Map<MonthKey, BookLineExpense> initDates = new LinkedHashMap<>();

        LocalDate currentDate = dates.start();
        while (!currentDate.isAfter(dates.end())) {
            initDates.put(MonthKey.of(currentDate, INCOME),
                BookLineExpense.initExpense(currentDate, INCOME));

            initDates.put(MonthKey.of(currentDate, OUTCOME),
                BookLineExpense.initExpense(currentDate, OUTCOME));

            currentDate = currentDate.plusDays(NEXT_DAY);
        }

        return initDates;
    }

    public static LocalDate formatToDate(LocalDateTime createdAt) {
        return createdAt.toLocalDate();
    }
}

