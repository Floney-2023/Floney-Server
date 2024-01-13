package com.floney.floney.common.domain.vo;

import com.floney.floney.book.dto.constant.DayType;
import com.floney.floney.book.dto.constant.ExcelDuration;
import com.floney.floney.common.exception.book.LimitRequestException;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static com.floney.floney.book.dto.constant.DayType.*;

@RequiredArgsConstructor
@Getter
public class DateDuration {

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

    public static DateDuration getAssetDuration(LocalDate currentDate) {
        // 현재 날짜로부터 5개월 이전의 날짜 계산
        LocalDate beforeMonth = getDateBeforeMonth(currentDate, FIVE_MOTH);
        return new DateDuration(beforeMonth, currentDate);
    }

    // 현시점으로 부터 특정 개월 이후의 기간을 반환한다. ex. 1년 - 1월 1일 ~ 12월 31일
    public static DateDuration getAfterMonthDuration(LocalDate firstDayOfMonth, DayType month) {
        LocalDate afterMonth = firstDayOfMonth.plusMonths(month.getValue());
        return new DateDuration(firstDayOfMonth, afterMonth.minusDays(1));
    }

    public static DateDuration getFirstAndEndDayOfYear(LocalDate firstDate) {
        LocalDate startDate = firstDate.withDayOfYear(FIRST_DAY.getValue());
        LocalDate endDate = firstDate.withDayOfYear(firstDate.lengthOfYear());
        return new DateDuration(startDate, endDate);
    }

    public static DateDuration getLastMonthDateDuration(LocalDate targetDate) {
        LocalDate before = getDateBeforeMonth(targetDate, ONE_MONTH);
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
                return getAfterMonthDuration(targetDate, DayType.ONE_YEAR_TO_MONTH);
            }
            default -> {
                throw new LimitRequestException();
            }
        }
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

    public LocalDate start() {
        return startDate;
    }

    public LocalDate end() {
        return endDate;
    }

}
