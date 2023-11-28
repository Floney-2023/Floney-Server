package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.CarryOver;
import com.floney.floney.common.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CarryOverRepository extends JpaRepository<CarryOver, Long>, CarryOverCustomRepository {
    Optional<CarryOver> findCarryOverByDateAndBookAndStatus(LocalDate targetDate, Book book, Status status);

}
