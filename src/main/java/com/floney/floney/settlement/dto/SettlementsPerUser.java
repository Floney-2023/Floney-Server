package com.floney.floney.settlement.dto;

import com.floney.floney.settlement.dto.SettlementPerUser;
import com.floney.floney.settlement.entity.SettlementUser;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class SettlementsPerUser {

    private final List<SettlementPerUser> settlementsPerUser;

    private SettlementsPerUser() {
        this.settlementsPerUser = new ArrayList<>();
    }

    public static SettlementsPerUser init() {
        return new SettlementsPerUser();
    }

    public void add(SettlementUser settlementUser) {
        settlementsPerUser.add(SettlementPerUser.builder()
                .nickname(settlementUser.getUser().getNickname())
                .money(settlementUser.getMoney())
                .build());
    }
}
