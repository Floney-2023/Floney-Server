package com.floney.floney.book.repository.category;

import com.floney.floney.book.entity.BookLineCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookLineCategoryRepository extends JpaRepository<BookLineCategory, Long>, BookLineCategoryCustomRepository {
}
