package com.floney.floney.settlement.dto.request;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@ToString
public class OutcomeRequest {

    @NotNull
    private Long outcome;
    @NotNull
    private String userEmail;
}
