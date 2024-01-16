package com.floney.floney.common.domain.vo;

import com.floney.floney.book.domain.constant.ExcelDuration;
import com.floney.floney.book.util.DateUtil;
import com.floney.floney.common.exception.book.LimitRequestException;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.floney.floney.book.util.DateUtil.getDateBeforeMonth;

@RequiredArgsConstructor
@Getter
public class DateDuration {
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

    public static DateDuration startAndEndOfMonth(String currentDate) {
        LocalDate startDate = LocalDate.parse(currentDate, DateTimeFormatter.ISO_DATE);
        LocalDate endDate = DateUtil.getLastDateOfMonth(startDate);
        return new DateDuration(startDate, endDate);
    }

    // 현재 날짜로부터 N개월 이전의 날짜 계산
    public static DateDuration beforeMonthToCurrent(LocalDate currentDate, int beforeMonth) {
        return new DateDuration(getDateBeforeMonth(currentDate, beforeMonth), currentDate);
    }

    // 현시점으로 부터 특정 개월 이후의 기간을 반환한다. ex. 1년 - 1월 1일 ~ 12월 31일
    public static DateDuration currentToAfterMonth(LocalDate firstDayOfMonth, int month) {
        LocalDate afterMonth = firstDayOfMonth.plusMonths(month);
        return new DateDuration(firstDayOfMonth, afterMonth.minusDays(ONE_DAY));
    }

    public static DateDuration firstAndEndDayOfYear(LocalDate firstDate) {
        LocalDate startDate = firstDate.withDayOfYear(FIRST_DAY_OF_YEAR);
        LocalDate endDate = firstDate.withDayOfYear(firstDate.lengthOfYear());
        return new DateDuration(startDate, endDate);
    }

    public static DateDuration firstAndLastDayFromLastMonth(LocalDate targetDate) {
        LocalDate startDate = getDateBeforeMonth(targetDate, BEFORE_ONE_MONTH);
        LocalDate endDate = DateUtil.getLastDateOfMonth(startDate);
        return new DateDuration(startDate, endDate);
    }

    public static DateDuration durationByExcelDuration(String currentDate, ExcelDuration excelDuration) {
        LocalDate targetDate = LocalDate.parse(currentDate, DateTimeFormatter.ISO_DATE);

        switch (excelDuration) {
            case THIS_MONTH -> {
                return startAndEndOfMonth(currentDate);
            }
            case LAST_MONTH -> {
                return firstAndLastDayFromLastMonth(targetDate);
            }
            case ONE_YEAR -> {
                return currentToAfterMonth(targetDate, ONE_YEAR_TO_MONTH);
            }
            default -> {
                throw new LimitRequestException();
            }
        }
    }

}
