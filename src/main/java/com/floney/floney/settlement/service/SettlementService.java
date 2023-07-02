package com.floney.floney.settlement.service;

import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.NotFoundBookException;
import com.floney.floney.common.exception.UserNotFoundException;
import com.floney.floney.settlement.dto.SettlementsPerUser;
import com.floney.floney.settlement.dto.request.OutcomeRequest;
import com.floney.floney.settlement.dto.request.SettlementRequest;
import com.floney.floney.settlement.dto.response.SettlementResponse;
import com.floney.floney.settlement.entity.Settlement;
import com.floney.floney.settlement.entity.SettlementUser;
import com.floney.floney.settlement.repository.SettlementRepository;
import com.floney.floney.settlement.repository.SettlementUserRepository;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final SettlementUserRepository settlementUserRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public SettlementResponse create(SettlementRequest request) {
        Settlement settlement = createSettlement(request);
        Map<Long, Long> outcomePerUserId = createOutcomePerUserId(request);
        SettlementsPerUser settlementsPerUser = createSettlementUsers(settlement, outcomePerUserId);
        return SettlementResponse.from(settlement, settlementsPerUser);
    }

    @Transactional
    public Settlement createSettlement(SettlementRequest request) {
        Settlement settlement = Settlement.builder()
                .book(bookRepository.findBookByIdAndStatus(request.getBookId(), Status.ACTIVE).orElseThrow(NotFoundBookException::new))
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalOutcome(calculateTotalMoney(request.getOutcomes()))
                .userCount(request.getUserIds().size())
                .build();
        return settlementRepository.save(settlement);
    }

    @Transactional
    public SettlementsPerUser createSettlementUsers(Settlement settlement, Map<Long, Long> outcomePerUserId) {
        SettlementsPerUser settlementsPerUser = SettlementsPerUser.init();
        Long outcomeAvg = settlement.getTotalOutcome() / settlement.getUserCount();

        for (Long userId : outcomePerUserId.keySet()) {
            User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
            Long money = outcomePerUserId.get(userId) - outcomeAvg;
            settlementsPerUser.add(createSettlementUser(settlement, user, money));
        }

        return settlementsPerUser;
    }

    @Transactional
    public SettlementUser createSettlementUser(Settlement settlement, User user, Long money) {
        SettlementUser settlementUser = SettlementUser.builder()
                .settlement(settlement)
                .user(user)
                .money(money)
                .build();
        return settlementUserRepository.save(settlementUser);
    }

    private Map<Long, Long> createOutcomePerUserId(SettlementRequest request) {
        Map<Long, Long> outcomePerUserId = request.getUserIds().stream().collect(Collectors.toMap(key -> key, value -> 0L));
        for (OutcomeRequest outcomeRequest : request.getOutcomes()) {
            Long outcome = outcomeRequest.getOutcome() + outcomePerUserId.get(outcomeRequest.getUserId());
            outcomePerUserId.put(outcomeRequest.getUserId(), outcome);
        }
        return outcomePerUserId;
    }

    private Long calculateTotalMoney(List<OutcomeRequest> outcomes) {
        return outcomes.stream().mapToLong(OutcomeRequest::getOutcome).sum();
    }
}
