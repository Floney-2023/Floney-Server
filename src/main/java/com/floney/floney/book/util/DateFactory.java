package com.floney.floney.book.util;

import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.book.dto.constant.DayType;
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

        // 각 달의 끝이 28,30,31중 어느날인지 계산
        YearMonth currentMonth = YearMonth.from(startDate);
        LocalDate endDate = currentMonth.atEndOfMonth();

        return DatesDuration.builder()
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

    public static DatesDuration getAssetDuration(LocalDate currentDate) {
        // 현재 날짜로부터 5개월 이전의 날짜 계산
        LocalDate beforeMonth = getDateBeforeMonth(currentDate, FIVE_MOTH);

        return DatesDuration.builder()
            .startDate(beforeMonth)
            .endDate(currentDate)
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
        LocalDate before = getDateBeforeMonth(targetDate, ONE_MONTH);
        YearMonth yearMonth = YearMonth.from(before);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return DatesDuration.builder()
            .startDate(before)
            .endDate(endDate)
            .build();
    }

    private static LocalDate getDateBeforeMonth(LocalDate targetDate, DayType dayType) {
        return targetDate.minusMonths(dayType.getValue());
    }

    // 해당 월의 일별로 지출, 수입 초기화 객체를 만들어주는 메서드
    // ex. { { "2024-01-09" : INCOME } : { date  : "2024-01-09" , money : 0.0 , assetType : INCOME } }
    // ex. { { "2024-01-09" : OUTCOME } : { date  : "2024-01-09" , money : 0.0 , assetType : OUTCOME } }
    public static Map<MonthKey, BookLineExpense> getInitBookLineExpenseByMonth(String targetDate) {
        DatesDuration dates = getStartAndEndOfMonth(targetDate);
        Map<MonthKey, BookLineExpense> initDates = new LinkedHashMap<>();

        LocalDate currentDate = dates.start();
        while (!currentDate.isAfter(dates.end())) {
            addExpense(initDates, currentDate, INCOME);
            addExpense(initDates, currentDate, OUTCOME);
            currentDate = currentDate.plusDays(ONE_DAY.getValue());
        }

        return initDates;
    }

    private static void addExpense(Map<MonthKey, BookLineExpense> initDates, LocalDate currentDate, AssetType type) {
        initDates.put(MonthKey.of(currentDate, type), BookLineExpense.initExpense(currentDate, type));
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

