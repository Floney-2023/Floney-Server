package com.floney.floney.book.repository;

import com.floney.floney.book.dto.BookLineExpense;
import com.floney.floney.book.dto.CalendarTotalExpense;

import java.time.LocalDate;
import java.util.List;

public interface BookLineCustomRepository{

    List<CalendarTotalExpense> totalExpense(String bookKey, LocalDate start, LocalDate end);

    List<BookLineExpense> dayIncomeAndOutcome(String bookKey, LocalDate start, LocalDate end);
}
