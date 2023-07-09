package com.floney.floney.settlement.dto;

import com.floney.floney.settlement.dto.request.OutcomeRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OutcomesWithUser {

    private final Map<Long, Long> outcomesWithUser;

    private OutcomesWithUser(Set<Long> userIds, Long leaderUserId) {
        this.outcomesWithUser = userIds.stream().collect(Collectors.toMap(key -> key, value -> 0L));
        this.outcomesWithUser.put(leaderUserId, 0L);
    }

    public static OutcomesWithUser init(Set<Long> userIds, Long leaderUserId) {
        return new OutcomesWithUser(userIds, leaderUserId);
    }

    public void fillOutcomes(List<OutcomeRequest> outcomeRequests) {
        outcomeRequests.forEach(outcome -> outcomesWithUser.put(
                outcome.getUserId(), outcomesWithUser.get(outcome.getUserId()) + outcome.getOutcome()
        ));
    }

    public Map<Long, Long> getOutcomesWithUser() {
        return Collections.unmodifiableMap(outcomesWithUser);
    }
}
