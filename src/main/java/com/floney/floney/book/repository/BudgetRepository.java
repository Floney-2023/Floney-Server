package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Budget;
import com.floney.floney.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findBudgetByBookAndDate(Book savedBook, LocalDate date);
}
