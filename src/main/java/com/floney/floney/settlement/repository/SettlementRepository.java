package com.floney.floney.settlement.repository;

import com.floney.floney.book.entity.Book;
import com.floney.floney.common.constant.Status;
import com.floney.floney.settlement.domain.entity.Settlement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long>, SettlementCustomRepository {

    List<Settlement> findAllByBookAndStatus(Book book, Status status);
}
