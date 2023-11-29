package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long>, BudgetCustomRepository {

    Optional<Budget> findBudgetByBookAndDate(Book savedBook, LocalDate date);

    List<Budget> findAllByBook(Book book);
}
