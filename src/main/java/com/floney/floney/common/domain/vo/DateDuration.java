package com.floney.floney.common.domain.vo;

import com.floney.floney.book.dto.constant.ExcelDuration;
import com.floney.floney.common.exception.book.LimitRequestException;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Getter
public class DateDuration {
    private static final int FIRST_DAY_OF_MONTH = 1;
    private static final int FIRST_DAY_OF_YEAR = 1;
    private static final int BEFORE_ONE_MONTH = 1;
    private static final int ASSET_DURATION = 5;
    private static final int ONE_DAY = 1;
    private static final int ONE_YEAR_TO_MONTH = 12;

    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    private DateDuration(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static DateDuration getStartAndEndOfMonth(String targetDate) {
        LocalDate startDate = LocalDate.parse(targetDate, DateTimeFormatter.ISO_DATE);

        // 각 달의 끝이 28,30,31중 어느날인지 계산
        YearMonth currentMonth = YearMonth.from(startDate);
        LocalDate endDate = currentMonth.atEndOfMonth();

        return new DateDuration(startDate, endDate);
    }

    // 현재 날짜로부터 N개월 이전의 날짜 계산
    public static DateDuration getBeforeMonthToCurrentDuration(LocalDate currentDate, int beforeMonth) {
        return new DateDuration(getDateBeforeMonth(currentDate, beforeMonth), currentDate);
    }

    // 현시점으로 부터 특정 개월 이후의 기간을 반환한다. ex. 1년 - 1월 1일 ~ 12월 31일
    public static DateDuration getAfterMonthDuration(LocalDate firstDayOfMonth, int month) {
        LocalDate afterMonth = firstDayOfMonth.plusMonths(month);
        return new DateDuration(firstDayOfMonth, afterMonth.minusDays(ONE_DAY));
    }

    public static DateDuration getFirstAndEndDayOfYear(LocalDate firstDate) {
        LocalDate startDate = firstDate.withDayOfYear(FIRST_DAY_OF_YEAR);
        LocalDate endDate = firstDate.withDayOfYear(firstDate.lengthOfYear());
        return new DateDuration(startDate, endDate);
    }

    public static DateDuration getLastMonthDateDuration(LocalDate targetDate) {
        LocalDate before = getDateBeforeMonth(targetDate, BEFORE_ONE_MONTH);
        YearMonth yearMonth = YearMonth.from(before);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return new DateDuration(before, endDate);
    }

    public static DateDuration getDurationByExcelDuration(String currentDate, ExcelDuration excelDuration) {
        LocalDate targetDate = LocalDate.parse(currentDate, DateTimeFormatter.ISO_DATE);

        switch (excelDuration) {
            case THIS_MONTH -> {
                return getStartAndEndOfMonth(currentDate);
            }
            case LAST_MONTH -> {
                return getLastMonthDateDuration(targetDate);
            }
            case ONE_YEAR -> {
                return getAfterMonthDuration(targetDate, ONE_YEAR_TO_MONTH);
            }
            default -> {
                throw new LimitRequestException();
            }
        }
    }

    public static boolean isFirstDay(String date) {
        return LocalDate.parse(date).getDayOfMonth() == FIRST_DAY_OF_MONTH;
    }

    public static LocalDate getFirstDayOfMonth(LocalDate requestDate) {
        return requestDate.withDayOfMonth(FIRST_DAY_OF_MONTH);
    }

    private static LocalDate getDateBeforeMonth(LocalDate targetDate, int beforeMonth) {
        return targetDate.minusMonths(beforeMonth);
    }

    public LocalDate start() {
        return startDate;
    }

    public LocalDate end() {
        return endDate;
    }

}
