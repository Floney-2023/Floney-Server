package com.floney.floney.settlement.repository;

import com.floney.floney.book.entity.Book;
import com.floney.floney.common.constant.Status;
import com.floney.floney.settlement.domain.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long>, SettlementCustomRepository {

    List<Settlement> findAllByBookAndStatusOrderByIdDesc(Book book, Status status);
}
