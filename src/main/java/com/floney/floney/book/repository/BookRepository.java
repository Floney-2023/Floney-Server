package com.floney.floney.book.repository;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.common.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, BookCustomRepository {

    Optional<Book> findBookByCodeAndStatus(String code, Status status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Book> findBookExclusivelyByCodeAndStatus(String code, Status status);

    Optional<Book> findBookByBookKeyAndStatus(String bookKey, Status status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Book> findBookExclusivelyByBookKeyAndStatus(String bookKey, Status status);
}
