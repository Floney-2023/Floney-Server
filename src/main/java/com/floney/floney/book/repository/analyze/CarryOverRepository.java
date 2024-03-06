package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.CarryOver;
import com.floney.floney.common.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface CarryOverRepository extends JpaRepository<CarryOver, Long>, CarryOverCustomRepository {
    Optional<CarryOver> findCarryOverByDateAndBookAndStatus(LocalDate targetDate, Book book, Status status);

    @Modifying(clearAutomatically = true)
    @Query(
        value = "insert into carry_over (date, money, book_id) values (:date, :money, :book) " +
            "on duplicate key update money = money + :money, updated_at = now()",
        nativeQuery = true
    )
    void upsertMoneyByDateAndBook(LocalDate date, Book book, double money);

}
