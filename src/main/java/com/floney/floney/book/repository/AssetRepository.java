package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Asset;
import com.floney.floney.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset,Long> {
    Optional<Asset> findAssetByBookAndDate(Book savedBook, LocalDate date);

    List<Asset> findAllByBook(Book book);
}
