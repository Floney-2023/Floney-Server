package com.floney.floney.settlement.repository;

import com.floney.floney.settlement.domain.entity.Settlement;
import java.util.List;

public interface SettlementCustomRepository {

    List<Settlement> findSettlementHaveToDelete();
}
