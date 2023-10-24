package com.floney.floney.settlement.repository;

import com.floney.floney.common.constant.Status;
import com.floney.floney.settlement.domain.entity.Settlement;
import com.floney.floney.settlement.domain.entity.SettlementUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementUserRepository extends JpaRepository<SettlementUser, Long>, SettlementUserCustomRepository {

    List<SettlementUser> findAllBySettlementAndStatus(Settlement settlement, Status status);
}
