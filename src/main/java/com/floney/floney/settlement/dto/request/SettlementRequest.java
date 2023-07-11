package com.floney.floney.settlement.dto.request;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SettlementRequest {

    @NotNull private String bookKey;
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    @NotNull private Set<String> userEmails;
    @NotNull private List<OutcomeRequest> outcomes;
}