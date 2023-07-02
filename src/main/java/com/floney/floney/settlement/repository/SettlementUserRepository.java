package com.floney.floney.settlement.repository;

import com.floney.floney.settlement.entity.SettlementUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementUserRepository extends JpaRepository<SettlementUser, Long> {
}
