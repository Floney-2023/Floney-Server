package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Asset;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.common.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long>, AssetCustomRepository {
    Optional<Asset> findAssetByDateAndBookAndStatus(LocalDate targetDate, Book book, Status status);

    List<Asset> findByDateBetweenAndBookAndStatus(LocalDate startDate, LocalDate endDate, Book book, Status status);
}
