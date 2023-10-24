package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Book;
import com.floney.floney.common.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, BookCustomRepository {

    Optional<Book> findBookByCodeAndStatus(String code, Status status);

    Optional<Book> findBookByBookKeyAndStatus(String bookKey, Status status);
}
