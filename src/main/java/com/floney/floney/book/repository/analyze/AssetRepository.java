package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Asset;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.common.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long>, AssetCustomRepository {

    List<Asset> findByDateBetweenAndBookAndStatus(LocalDate startDate, LocalDate endDate, Book book, Status status);

    @Modifying
    @Query(
            value = "insert into ASSET (date, money, book_id) values (:date, :money, :book) " +
                    "on duplicate key update money = money + :money, updated_at = now()",
            nativeQuery = true
    )
    void upsertMoneyByDateAndBook(LocalDate date, Book book, double money);
}
