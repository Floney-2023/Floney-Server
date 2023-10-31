package com.floney.floney.settlement.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SettlementRequest {

    @NotNull(message = "bookKey를 입력해주세요")
    @NotBlank(message = "bookKey를 입력해주세요")
    private String bookKey;

    @NotNull(message = "startDate를 입력해주세요")
    private LocalDate startDate;

    @NotNull(message = "endDate를 입력해주세요")
    private LocalDate endDate;

    @NotNull(message = "userEmails를 입력해주세요")
    private Set<String> userEmails;

    @NotNull(message = "outcomes를 입력해주세요")
    private List<OutcomeRequest> outcomes;
}
