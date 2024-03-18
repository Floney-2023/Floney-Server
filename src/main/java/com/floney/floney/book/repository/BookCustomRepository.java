package com.floney.floney.book.repository;

import com.floney.floney.book.domain.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookCustomRepository {

    Optional<Book> findByBookUserEmailAndBookKey(String userEmail, String bookKey);

    List<Book> findAllByUserEmail(String userEmail);
}
