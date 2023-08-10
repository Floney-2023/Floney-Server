package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Book;
import com.floney.floney.common.constant.Status;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findBookByCodeAndStatus(String code, Status status);

    Optional<Book> findBookByBookKeyAndStatus(String bookKey, Status status);
}
