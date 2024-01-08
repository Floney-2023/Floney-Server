package com.floney.floney.category.repository;

import com.floney.floney.book.domain.entity.category.DefaultCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultCategoryDetailRepository extends JpaRepository<DefaultCategory, Long> {
}
