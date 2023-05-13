package com.floney.floney.book.repository;

import com.floney.floney.book.dto.BookLineExpense;
import com.floney.floney.book.dto.TotalExpense;
import com.floney.floney.book.dto.DayLine;

import java.time.LocalDate;
import java.util.List;

public interface BookLineCustomRepository{

    List<TotalExpense> totalExpense(String bookKey, LocalDate start, LocalDate end);

    List<DayLine> allLinesByDay(LocalDate date, String bookKey);

    List<TotalExpense> totalExpenseByDay(LocalDate date, String bookKey);

    List<BookLineExpense> dayIncomeAndOutcome(String bookKey, LocalDate start, LocalDate end);
}
