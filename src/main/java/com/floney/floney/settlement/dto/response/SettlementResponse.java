package com.floney.floney.settlement.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.floney.floney.settlement.domain.entity.Settlement;
import com.floney.floney.settlement.domain.entity.SettlementUser;
import com.floney.floney.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@JsonInclude(Include.NON_NULL)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SettlementResponse {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer userCount;
    private double totalOutcome;
    private double outcome;
    private List<DetailResponse> details;

    public static SettlementResponse from(Settlement settlement) {
        return SettlementResponse.builder()
            .id(settlement.getId())
            .startDate(settlement.getStartDate())
            .endDate(settlement.getEndDate())
            .userCount(settlement.getUserCount())
            .totalOutcome(settlement.getTotalOutcome())
            .outcome(settlement.getAvgOutcome())
            .build();
    }

    public static SettlementResponse of(Settlement settlement, List<SettlementUser> settlementUsers) {
        return SettlementResponse.builder()
            .id(settlement.getId())
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

        private String userNickname;
        private String userProfileImg;
        private Double money;

        private static DetailResponse from(final SettlementUser settlementUser) {
            if (settlementUser.getUser() == null || !settlementUser.isActive()) {
                return DetailResponse.builder()
                    .userNickname(User.DELETE_VALUE)
                    .userProfileImg(null)
                    .money(settlementUser.getMoney())
                    .build();
            }
            return DetailResponse.builder()
                .userNickname(settlementUser.getUser().getNickname())
                .userProfileImg(settlementUser.getUser().getProfileImg())
                .money(settlementUser.getMoney())
                .build();
        }
    }
}
