package com.floney.floney.book.repository;

import com.floney.floney.book.entity.RepeatCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepeatCategoryRepository extends JpaRepository<RepeatCategory, Long> {
    Optional<RepeatCategory> findRepeatCategoryByKind(String repeat);
}
