package com.floney.floney.settlement.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.floney.floney.settlement.entity.Settlement;
import com.floney.floney.settlement.entity.SettlementUser;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(Include.NON_NULL)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SettlementResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private Integer userCount;
    private Long totalOutcome;
    private Long outcome;
    private List<DetailResponse> details;

    public static SettlementResponse from(Settlement settlement) {
        return SettlementResponse.builder()
                .startDate(settlement.getStartDate())
                .endDate(settlement.getEndDate())
                .userCount(settlement.getUserCount())
                .totalOutcome(settlement.getTotalOutcome())
                .outcome(settlement.getAvgOutcome())
                .build();
    }

    public static SettlementResponse of(Settlement settlement, List<SettlementUser> settlementUsers) {
        return SettlementResponse.builder()
                .startDate(settlement.getStartDate())
                .endDate(settlement.getEndDate())
                .userCount(settlement.getUserCount())
                .totalOutcome(settlement.getTotalOutcome())
                .outcome(settlement.getAvgOutcome())
                .details(settlementUsers.stream().map(DetailResponse::from).toList())
                .build();
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class DetailResponse {

        private String UserNickname;
        private Long money;

        private static DetailResponse from(SettlementUser settlementUser) {
            return DetailResponse.builder()
                    .UserNickname(settlementUser.getUser().getNickname())
                    .money(settlementUser.getMoney())
                    .build();
        }
    }
}
