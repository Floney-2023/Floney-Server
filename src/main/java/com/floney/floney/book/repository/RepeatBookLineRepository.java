package com.floney.floney.book.repository;

import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.RepeatBookLine;
import com.floney.floney.common.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepeatBookLineRepository extends JpaRepository<RepeatBookLine, Long>, RepeatBookLineCustomRepository {
    List<RepeatBookLine> findAllByBookAndStatusAndLineCategory(Book book, Status status, Category category);

    Optional<RepeatBookLine> findByIdAndStatus(long repeatLineId, Status status);
}
