package com.floney.floney.book.repository;

import com.floney.floney.book.entity.BookAnalyze;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookAnalyzeRepository extends JpaRepository<BookAnalyze, Long> , BookAnalyzeCustomRepository {
}
