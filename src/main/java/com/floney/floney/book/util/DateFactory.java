package com.floney.floney.book.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateFactory {

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

    public static List<LocalDate> generateDatStorage(String targetDate) {
        Map<String,LocalDate> dates = getDate(targetDate);
        List<LocalDate> dateList = new ArrayList<>();

        LocalDate currentDate = dates.get(START);
        while (!currentDate.isAfter(dates.get(END))) {
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        return dateList;
    }
}

