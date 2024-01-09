package com.floney.floney.book.util;

import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.dto.process.MonthKey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.floney.floney.book.dto.constant.AssetType.INCOME;
import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;
import static com.floney.floney.book.dto.constant.DayType.*;

public class DateFactory {

    public static DatesDuration getStartAndEndOfMonth(String targetDate) {
        LocalDate startDate = LocalDate.parse(targetDate, DateTimeFormatter.ISO_DATE);

        // 각 달의 끝이 28,30,31일 중 어떤건지 계산
        YearMonth currentMonth = YearMonth.from(startDate);
        LocalDate endDate = currentMonth.atEndOfMonth();

        return DatesDuration.builder()
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

    public static DatesDuration getAssetDuration(LocalDate date) {
        // 현재 날짜로부터 5개월 이전의 날짜 계산
        LocalDate startDate = date.minusMonths(FIVE_MOTH.getValue());

        return DatesDuration.builder()
            .startDate(startDate)
            .endDate(date)
            .build();
    }

    public static DatesDuration getYearDuration(LocalDate firstDate) {
        LocalDate startDate = firstDate.withDayOfYear(FIRST_DAY.getValue());
        LocalDate endDate = firstDate.withDayOfYear(firstDate.lengthOfYear());

        return DatesDuration.builder()
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

    public static DatesDuration getBeforeDateDuration(LocalDate targetDate) {
        LocalDate before = getBeforeMonth(targetDate);
        YearMonth yearMonth = YearMonth.from(before);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return DatesDuration.builder()
            .startDate(before)
            .endDate(endDate)
            .build();
    }

    public static LocalDate getBeforeMonth(LocalDate targetDate) {
        return targetDate.minusMonths(ONE_MONTH.getValue());
    }

    public static Map<MonthKey, BookLineExpense> initDates(String targetDate) {
        DatesDuration dates = getStartAndEndOfMonth(targetDate);
        Map<MonthKey, BookLineExpense> initDates = new LinkedHashMap<>();

        LocalDate currentDate = dates.start();
        while (!currentDate.isAfter(dates.end())) {
            initDates.put(MonthKey.of(currentDate, INCOME),
                BookLineExpense.initExpense(currentDate, INCOME));

            initDates.put(MonthKey.of(currentDate, OUTCOME),
                BookLineExpense.initExpense(currentDate, OUTCOME));

            currentDate = currentDate.plusDays(ONE_DAY.getValue());
        }

        return initDates;
    }

    public static LocalDate formatToDate(LocalDateTime createdAt) {
        return createdAt.toLocalDate();
    }

    public static boolean isFirstDay(String date) {
        return LocalDate.parse(date).getDayOfMonth() == FIRST_DAY.getValue();
    }

    public static LocalDate getFirstDayOf(LocalDate requestDate) {
        return requestDate.withDayOfMonth(FIRST_DAY.getValue());
    }
}

