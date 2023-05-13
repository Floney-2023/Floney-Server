package com.floney.floney.book.repository;

import com.floney.floney.book.dto.BookLineExpense;
import com.floney.floney.book.dto.CalendarTotalExpense;
import com.floney.floney.book.dto.DayLine;
import com.querydsl.core.Tuple;

import java.time.LocalDate;
import java.util.List;

public interface BookLineCustomRepository{

    List<CalendarTotalExpense> totalExpense(String bookKey, LocalDate start, LocalDate end);

    List<DayLine> allLinesByDay(LocalDate date, String bookKey);


    List<Tuple> test(LocalDate date, String bookKey);

    List<BookLineExpense> dayIncomeAndOutcome(String bookKey, LocalDate start, LocalDate end);
}
