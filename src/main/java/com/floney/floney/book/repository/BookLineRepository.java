package com.floney.floney.book.repository;

import com.floney.floney.book.entity.BookLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookLineRepository extends JpaRepository<BookLine,Long> {
}
