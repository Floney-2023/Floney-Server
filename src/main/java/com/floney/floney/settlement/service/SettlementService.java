package com.floney.floney.settlement.service;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import com.floney.floney.common.exception.settlement.SettlementNotFoundException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.settlement.domain.OutcomesWithUser;
import com.floney.floney.settlement.domain.entity.Settlement;
import com.floney.floney.settlement.domain.entity.SettlementUser;
import com.floney.floney.settlement.dto.request.SettlementRequest;
import com.floney.floney.settlement.dto.response.SettlementResponse;
import com.floney.floney.settlement.repository.SettlementRepository;
import com.floney.floney.settlement.repository.SettlementUserRepository;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final SettlementUserRepository settlementUserRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;

    public List<SettlementResponse> findAll(String bookKey) {
        final Book book = findBookByBookKey(bookKey);

        return findSettlementsOrderByRecentTime(book)
                .stream()
                .map(SettlementResponse::from)
                .toList();
    }

    public SettlementResponse find(Long id) {
        final Settlement settlement = findSettlementById(id);
        final List<SettlementUser> settlementUsers = findSettlementUsersBySettlement(settlement);
        return SettlementResponse.of(settlement, settlementUsers);
    }

    @Transactional
    public SettlementResponse create(SettlementRequest request) {
        final Settlement settlement = createSettlement(request);

        validateBookUsers(settlement.getBook().getBookKey(), request.getUserEmails());
        final List<SettlementUser> settlementUsers = createSettlementUsers(request, settlement);

        settlement.updateBookLastSettlementDate();

        return SettlementResponse.of(settlement, settlementUsers);
    }

    @Transactional
    public void deleteAllBy(final long bookId) {
        settlementRepository.inactiveAllByBookId(bookId);
        settlementUserRepository.inactiveAllByBookId(bookId);
    }

    private static OutcomesWithUser createOutcomesWithUser(SettlementRequest request) {
        final OutcomesWithUser outcomesWithUser = OutcomesWithUser.init(request.getUserEmails());
        outcomesWithUser.fillOutcomes(request.getOutcomes());
        return outcomesWithUser;
    }

    private Settlement findSettlementById(final Long id) {
        return settlementRepository.findById(id)
                .orElseThrow(SettlementNotFoundException::new);
    }

    private List<Settlement> findSettlementsOrderByRecentTime(final Book book) {
        return settlementRepository.findAllByBookAndStatusOrderByIdDesc(book, Status.ACTIVE);
    }

    private List<SettlementUser> findSettlementUsersBySettlement(final Settlement settlement) {
        return settlementUserRepository.findAllBySettlementAndStatus(settlement, Status.ACTIVE);
    }

    private Book findBookByBookKey(final String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE)
                .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private void validateBookUsers(final String bookKey, final Set<String> emails) {
        emails.forEach(email -> validateBookUser(bookKey, email));
    }

    private void validateBookUser(final String bookKey, final String email) {
        if (!bookUserRepository.existsByBookKeyAndUserEmail(bookKey, email)) {
            throw new NotFoundBookUserException(bookKey, email);
        }
    }

    private Settlement createSettlement(SettlementRequest request) {
        final Book book = findBookByBookKey(request.getBookKey());
        return settlementRepository.save(Settlement.of(book, request));
    }

    private List<SettlementUser> createSettlementUsers(SettlementRequest request, Settlement settlement) {
        final OutcomesWithUser outcomesWithUser = createOutcomesWithUser(request);
        return createSettlementUsers(settlement, outcomesWithUser);
    }

    private List<SettlementUser> createSettlementUsers(Settlement settlement, OutcomesWithUser outcomesWithUser) {
        final double avgOutcome = settlement.getAvgOutcome();
        final List<SettlementUser> settlementUsers = new ArrayList<>();

        outcomesWithUser.getOutcomesWithUser().forEach((userEmail, outcome) -> {
            User user = findUserByEmail(userEmail);
            settlementUsers.add(SettlementUser.of(settlement, user, outcome - avgOutcome));
        });

        return settlementUserRepository.saveAll(settlementUsers);
    }

    private User findUserByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
