package com.floney.floney.analyze.repository;

import com.floney.floney.analyze.entity.Asset;
import com.floney.floney.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset,Long> {
    Optional<Asset> findAssetByBookAndDate(Book savedBook, LocalDate date);
}