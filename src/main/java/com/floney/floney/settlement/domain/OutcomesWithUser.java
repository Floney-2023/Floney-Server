package com.floney.floney.settlement.domain;

import com.floney.floney.common.exception.settlement.OutcomeUserNotFoundException;
import com.floney.floney.settlement.dto.request.OutcomeRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OutcomesWithUser {

    private final Map<String, Long> outcomesWithUser;

    private OutcomesWithUser(Set<String> userEmails) {
        this.outcomesWithUser = userEmails.stream()
                .collect(Collectors.toMap(key -> key, value -> 0L));
    }

    public static OutcomesWithUser init(Set<String> userEmails) {
        return new OutcomesWithUser(userEmails);
    }

    public void fillOutcomes(List<OutcomeRequest> outcomeRequests) {
        try {
            outcomeRequests.forEach(outcome -> outcomesWithUser.put(
                    outcome.getUserEmail(),
                    outcomesWithUser.get(outcome.getUserEmail()) + outcome.getOutcome()
            ));
        } catch (NullPointerException exception) {
            throw new OutcomeUserNotFoundException();
        }
    }

    public Map<String, Long> getOutcomesWithUser() {
        return Collections.unmodifiableMap(outcomesWithUser);
    }
}
