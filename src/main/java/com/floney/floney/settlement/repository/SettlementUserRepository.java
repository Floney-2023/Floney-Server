package com.floney.floney.settlement.repository;

import com.floney.floney.common.constant.Status;
import com.floney.floney.settlement.entity.Settlement;
import com.floney.floney.settlement.entity.SettlementUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementUserRepository extends JpaRepository<SettlementUser, Long> {

    List<SettlementUser> findAllBySettlementAndStatus(Settlement settlement, Status status);
}