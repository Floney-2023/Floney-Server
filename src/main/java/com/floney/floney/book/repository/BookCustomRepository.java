package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Book;
import java.util.Optional;

public interface BookCustomRepository {

    Optional<Book> findByBookUserEmailAndBookKey(String userEmail, String bookKey);
}
