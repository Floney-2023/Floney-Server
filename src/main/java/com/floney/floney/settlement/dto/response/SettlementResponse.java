package com.floney.floney.settlement.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.floney.floney.settlement.dto.SettlementPerUser;
import com.floney.floney.settlement.dto.SettlementsPerUser;
import com.floney.floney.settlement.entity.Settlement;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SettlementResponse {

    private Long totalOutcome;
    private Long outcome;
    @JsonProperty(value = "settlement")
    private List<SettlementPerUser> settlementsPerUser;

    public static SettlementResponse from(Settlement settlement, SettlementsPerUser settlementsPerUser) {
        return SettlementResponse.builder()
                .totalOutcome(settlement.getTotalOutcome())
                .outcome(settlement.getTotalOutcome() / settlement.getUserCount())
                .settlementsPerUser(settlementsPerUser.getSettlementsPerUser())
                .build();
    }
}
