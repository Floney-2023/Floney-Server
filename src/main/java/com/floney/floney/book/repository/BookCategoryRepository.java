package com.floney.floney.book.repository;

import com.floney.floney.book.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCategoryRepository extends JpaRepository<BookCategory,Long> {
}
