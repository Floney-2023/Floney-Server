package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.entity.Asset;
import com.floney.floney.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findAssetByDateAndBook(LocalDate targetDate, Book book);

    List<Asset> findByDateBetweenAndBook(LocalDate startDate, LocalDate endDate,Book book);
}
