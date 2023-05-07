package com.floney.floney.book.repository;

import com.floney.floney.book.dto.BookLineExpense;

import java.time.LocalDate;
import java.util.List;

public interface BookLineCustomRepository{


    List<BookLineExpense> dayIncomeAndOutcome(String bookKey, LocalDate start, LocalDate end);
}
