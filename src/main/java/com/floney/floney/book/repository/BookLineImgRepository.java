package com.floney.floney.book.repository;

import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookLineImg;
import com.floney.floney.book.domain.entity.RepeatBookLine;
import com.floney.floney.common.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookLineImgRepository extends JpaRepository<BookLineImg, Long> {
    Optional<BookLineImg> findByIdAndStatus(long id, Status status);

    List<BookLineImg> findAllByBookLineAndStatus(BookLine bookLine, Status status);

    List<BookLineImg> findAllByRepeatBookLineAndStatus(RepeatBookLine repeatBookLine,Status status);
}