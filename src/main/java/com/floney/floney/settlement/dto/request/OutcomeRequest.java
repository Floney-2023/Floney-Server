package com.floney.floney.settlement.dto.request;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OutcomeRequest {

    @NotNull private Long outcome;
    @NotNull private String userEmail;
}
