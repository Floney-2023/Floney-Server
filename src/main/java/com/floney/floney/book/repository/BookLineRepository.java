package com.floney.floney.book.repository;

import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.common.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookLineRepository extends JpaRepository<BookLine, Long>, BookLineCustomRepository {
    Optional<BookLine> findByIdAndStatus(Long bookLineKey, Status status);


}
