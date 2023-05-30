package com.floney.floney.book.util;

import com.floney.floney.book.dto.BookLineExpense;
import com.floney.floney.book.dto.MonthKey;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.floney.floney.book.dto.constant.AssetType.INCOME;
import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;

public class DateFactory {

    public static final String START = "start";
    public static final String END = "end";
    public static final int NEXT_DAY = 1;
    public static Map<String, LocalDate> getDate(String targetDate) {
        LocalDate date = LocalDate.parse(targetDate, DateTimeFormatter.ISO_DATE);
        Map<String, LocalDate> dates = new HashMap<>();
        YearMonth yearMonth = YearMonth.from(date);
        LocalDate lastCurrentDate = yearMonth.atEndOfMonth();

        dates.put(START, date);
        dates.put(END, lastCurrentDate);
        return dates;
    }

    public static Map<MonthKey, BookLineExpense> initDates(String targetDate) {
        Map<String, LocalDate> dates = getDate(targetDate);
        Map<MonthKey, BookLineExpense> initDates = new LinkedHashMap<>();

        LocalDate currentDate = dates.get(START);
        while (!currentDate.isAfter(dates.get(END))) {
            initDates.put(MonthKey.of(currentDate, INCOME),
                BookLineExpense.initExpense(currentDate, INCOME));

            initDates.put(MonthKey.of(currentDate, OUTCOME),
                BookLineExpense.initExpense(currentDate, OUTCOME));

            currentDate = currentDate.plusDays(NEXT_DAY);
        }

        return initDates;
    }
}

