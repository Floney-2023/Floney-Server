package com.floney.floney.book.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

public class DateUtil {
    private static final int ONE_MONTH = 1;

    public static boolean isFirstDay(String date) {
        return LocalDate.parse(date).getDayOfMonth() == ONE_MONTH;
    }

    public static LocalDate getFirstDayOfMonth(LocalDate requestDate) {
        return requestDate.withDayOfMonth(ONE_MONTH);
    }

    public static LocalDate getDateBeforeMonth(LocalDate targetDate, int beforeMonth) {
        return targetDate.minusMonths(beforeMonth);
    }

    // 각 달의 끝이 28,30,31중 어느날인지 계산
    public static LocalDate getLastDateOfMonth(LocalDate currentDate) {
        return YearMonth.from(currentDate).atEndOfMonth();
    }


    public static boolean isWeekDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    public static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

}
