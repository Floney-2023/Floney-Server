package com.floney.floney.book.repository;

import com.floney.floney.book.entity.LineCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LineCategoryRepository extends JpaRepository<LineCategory,Long> {
    Optional<LineCategory> findLineCategoryByLine(String line);
}
