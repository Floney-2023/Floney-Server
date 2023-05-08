package com.floney.floney.book.dto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DateFormatter {

    public static final String START = "start";
    public static final String END = "end";

    public static Map<String, LocalDate> getDate(String targetDate) {
        LocalDate date = LocalDate.parse(targetDate, DateTimeFormatter.ISO_DATE);
        Map<String, LocalDate> dates = new HashMap<>();
        YearMonth yearMonth = YearMonth.from(date);
        LocalDate lastCurrentDate = yearMonth.atEndOfMonth();

        dates.put(START, date);
        dates.put(END, lastCurrentDate);
        return dates;
    }
}
