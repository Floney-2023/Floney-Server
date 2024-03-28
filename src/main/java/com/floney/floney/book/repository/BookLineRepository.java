package com.floney.floney.book.repository;

import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.RepeatBookLine;
import com.floney.floney.common.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookLineRepository extends JpaRepository<BookLine, Long>, BookLineCustomRepository {
    Optional<BookLine> findByIdAndStatus(Long bookLineKey, Status status);

    boolean existsBookLineByStatusAndRepeatBookLine(Status status, RepeatBookLine repetBookLine);


}
