package com.floney.floney.settlement.dto.request;

import com.floney.floney.user.dto.security.CustomUserDetails;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SettlementRequest {

    @NotNull private Long bookId;
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    @NotNull private Set<Long> userIds;
    @NotNull private List<OutcomeRequest> outcomes;

    public void addUser(CustomUserDetails userDetails) {
        userIds.add(userDetails.getUser().getId());
    }
}
