package com.floney.floney.book.util;

import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.book.dto.constant.DayType;
import com.floney.floney.book.dto.constant.ExcelDuration;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.dto.process.MonthKey;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.floney.floney.book.dto.constant.AssetType.INCOME;
import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;
import static com.floney.floney.book.dto.constant.DayType.*;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

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

    // 현시점으로 부터 특정 개월 이후의 기간을 반환한다. ex. 1년 - 1월 1일 ~ 12월 31일
    public static DatesDuration getAfterMonthDuration(LocalDate firstDayOfMonth, DayType month) {
        LocalDate afterMonth = firstDayOfMonth.plusMonths(month.getValue());
        return DatesDuration.builder()
                .startDate(firstDayOfMonth)
                .endDate(afterMonth.minusDays(1))
                .build();
    }

    public static DatesDuration getFirstAndEndDayOfWeek(LocalDate currentDate) {
        LocalDate monday = currentDate.with(previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = currentDate.with(nextOrSame(DayOfWeek.SUNDAY));

        return DatesDuration.builder()
                .startDate(monday)
                .endDate(sunday)
                .build();
    }

    public static DatesDuration getFirstAndEndDayOfYear(LocalDate firstDate) {
        LocalDate startDate = firstDate.withDayOfYear(FIRST_DAY.getValue());
        LocalDate endDate = firstDate.withDayOfYear(firstDate.lengthOfYear());

        return DatesDuration.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public static DatesDuration getLastMonthDateDuration(LocalDate targetDate) {
        LocalDate before = getDateBeforeMonth(targetDate, ONE_MONTH);
        YearMonth yearMonth = YearMonth.from(before);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return DatesDuration.builder()
                .startDate(before)
                .endDate(endDate)
                .build();
    }

    // 해당 월의 일별로 지출, 수입 초기화 객체를 만들어주는 메서드
    // return ex. { { "2024-01-09" : INCOME } : { date  : "2024-01-09" , money : 0.0 , assetType : INCOME } }
    // { { "2024-01-09" : OUTCOME } : { date  : "2024-01-09" , money : 0.0 , assetType : OUTCOME } }
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

    public static DatesDuration getDurationByExcelDuration(String currentDate, ExcelDuration excelDuration) {
        LocalDate targetDate = LocalDate.parse(currentDate, DateTimeFormatter.ISO_DATE);

        switch (excelDuration) {
            case THIS_MONTH -> {
                return getStartAndEndOfMonth(currentDate);
            }
            case LAST_MONTH -> {
                return getLastMonthDateDuration(targetDate);
            }
            case ONE_YEAR -> {
                return getAfterMonthDuration(targetDate, DayType.ONE_YEAR_TO_MONTH);
            }
            default -> {
                return null;
            }
        }
    }

    private static void addExpense(Map<MonthKey, BookLineExpense> initDates, LocalDate currentDate, AssetType type) {
        initDates.put(MonthKey.of(currentDate, type), BookLineExpense.initExpense(currentDate, type));
    }

    public static boolean isFirstDay(String date) {
        return LocalDate.parse(date).getDayOfMonth() == FIRST_DAY.getValue();
    }

    public static LocalDate getFirstDayOfMonth(LocalDate requestDate) {
        return requestDate.withDayOfMonth(FIRST_DAY.getValue());
    }

    private static LocalDate getDateBeforeMonth(LocalDate targetDate, DayType beforeMonth) {
        return targetDate.minusMonths(beforeMonth.getValue());
    }
}

