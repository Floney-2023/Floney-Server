package com.floney.floney.book.repository;

import com.floney.floney.book.dto.BookLineExpense;
import com.floney.floney.book.dto.TotalExpense;
import com.floney.floney.book.dto.DayLine;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookLineCustomRepository{

    Map<String,Long> totalExpenseByMonth(String bookKey, Map<String, LocalDate> dates);

    List<DayLine> allLinesByDay(LocalDate date, String bookKey);

    List<TotalExpense> totalExpenseByDay(LocalDate date, String bookKey);

    List<BookLineExpense> dayIncomeAndOutcome(String bookKey, Map<String, LocalDate> dates);
}
