package com.floney.floney.book.repository;

import com.floney.floney.book.entity.BookLineCategory;

import java.time.LocalDate;

public interface BookLineCustomRepository{

    Long dayIncome(String bookKey, LocalDate date);
}
