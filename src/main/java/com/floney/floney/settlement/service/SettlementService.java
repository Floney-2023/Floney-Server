package com.floney.floney.settlement.service;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.service.BookService;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.SettlementNotFoundException;
import com.floney.floney.common.exception.UserNotFoundException;
import com.floney.floney.settlement.dto.OutcomesWithUser;
import com.floney.floney.settlement.dto.request.SettlementRequest;
import com.floney.floney.settlement.dto.response.SettlementResponse;
import com.floney.floney.settlement.entity.Settlement;
import com.floney.floney.settlement.entity.SettlementUser;
import com.floney.floney.settlement.repository.SettlementRepository;
import com.floney.floney.settlement.repository.SettlementUserRepository;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final SettlementUserRepository settlementUserRepository;
    private final UserRepository userRepository;
    private final BookService bookService;

    @Transactional(readOnly = true)
    public List<SettlementResponse> findAll(String bookKey) {
        final Book book = bookService.findBook(bookKey);
        return settlementRepository.findAllByBookAndStatus(book, Status.ACTIVE)
                .stream()
                .map(SettlementResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SettlementResponse find(Long id) {
        final Settlement settlement = settlementRepository.findById(id).orElseThrow(SettlementNotFoundException::new);
        final List<SettlementUser> settlementUsers = settlementUserRepository.findAllBySettlementAndStatus(settlement, Status.ACTIVE);
        return SettlementResponse.of(settlement, settlementUsers);
    }

    @Transactional
    public SettlementResponse create(SettlementRequest request, CustomUserDetails userDetails) {
        Settlement settlement = createSettlement(request, userDetails.getUser());
        List<SettlementUser> settlementUsers = createSettlementUsers(request, settlement, userDetails.getUser());
        bookService.updateLastSettlementDate(settlement.getBook().getBookKey(), settlement.getCreatedAt().toLocalDate());
        return SettlementResponse.of(settlement, settlementUsers);
    }

    @Transactional
    public Settlement createSettlement(SettlementRequest request, User leaderUser) {
        final Book book = bookService.findBook(request.getBookKey());
        return settlementRepository.save(Settlement.of(book, request, leaderUser.getEmail()));
    }

    public List<SettlementUser> createSettlementUsers(SettlementRequest request, Settlement settlement, User leaderUser) {
        OutcomesWithUser outcomesWithUser = createOutcomesWithUser(request, leaderUser);
        return createSettlementUsers(settlement, outcomesWithUser);
    }

    @Transactional
    public List<SettlementUser> createSettlementUsers(Settlement settlement, OutcomesWithUser outcomesWithUser) {
        final Long avgOutcome = settlement.getAvgOutcome();
        List<SettlementUser> settlementUsers = new ArrayList<>();

        outcomesWithUser.getOutcomesWithUser().forEach((userEmail, outcome) -> {
            User user = userRepository.findByEmail(userEmail).orElseThrow(UserNotFoundException::new);
            settlementUsers.add(createSettlementUser(settlement, user, outcome - avgOutcome));
        });

        return settlementUserRepository.saveAll(settlementUsers);
    }

    private static OutcomesWithUser createOutcomesWithUser(SettlementRequest request, User leaderUser) {
        OutcomesWithUser outcomesWithUser = OutcomesWithUser.init(request.getUserEmails(), leaderUser.getEmail());
        outcomesWithUser.fillOutcomes(request.getOutcomes());
        return outcomesWithUser;
    }

    private SettlementUser createSettlementUser(Settlement settlement, User user, Long money) {
        return SettlementUser.builder()
                .settlement(settlement)
                .user(user)
                .money(money)
                .build();
    }
}
