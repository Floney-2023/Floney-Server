package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Budget;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long>, BudgetCustomRepository {

    Optional<Budget> findBudgetByBookAndDate(Book savedBook, LocalDate date);

    List<Budget> findAllByBook(Book book);
}
