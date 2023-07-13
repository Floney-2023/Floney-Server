package com.floney.floney.book.repository;

import com.floney.floney.book.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookLineCustomRepository {

    Map<String, Long> totalExpenseByMonth(String bookKey, DatesRequest dates);

    List<DayLineByDayView> allLinesByDay(LocalDate date, String bookKey);

    List<TotalExpense> totalExpenseByDay(LocalDate date, String bookKey);

    List<BookLineExpense> dayIncomeAndOutcome(String bookKey, DatesRequest dates);

    void deleteAllLines(String bookKey);

    List<DayLine> allOutcomes(AllOutcomesReqeust request);
}
