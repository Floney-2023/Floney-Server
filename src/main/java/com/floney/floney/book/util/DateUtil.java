package com.floney.floney.book.util;

import java.time.LocalDate;

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
}
