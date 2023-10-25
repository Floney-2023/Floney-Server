package com.floney.floney.settlement.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SettlementRequest {

    @NotNull
    private String bookKey;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private Set<String> userEmails;
    @NotNull
    private List<OutcomeRequest> outcomes;
}
