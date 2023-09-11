package com.floney.floney.book.repository;

import com.floney.floney.book.entity.CarryOver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CarryOverRepository extends JpaRepository<CarryOver,Long> {
    Optional<CarryOver> findCarryOverByDate(LocalDate targetDate);
}
