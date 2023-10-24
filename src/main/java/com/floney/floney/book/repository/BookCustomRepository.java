package com.floney.floney.book.repository;

import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.dto.response.BudgetYearResponse;
import com.floney.floney.book.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookCustomRepository {

    Optional<Book> findByBookUserEmailAndBookKey(String userEmail, String bookKey);

    List<BudgetYearResponse> findBudgetByYear(String bookKey, DatesDuration duration);
}
