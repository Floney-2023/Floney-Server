package com.floney.floney.book.repository;

import com.floney.floney.book.entity.BookAnalyze;

import java.time.LocalDate;

public interface BookAnalyzeCustomRepository {
    BookAnalyze findAnalyze(String bookKey, LocalDate date);
}
