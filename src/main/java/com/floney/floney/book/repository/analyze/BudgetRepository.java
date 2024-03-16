package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.Budget;
import com.floney.floney.common.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long>, BudgetCustomRepository {

    Optional<Budget> findBudgetByBookAndDateAndStatus(Book savedBook, LocalDate date, Status status);
}
