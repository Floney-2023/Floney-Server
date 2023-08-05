package com.floney.floney.settlement.service;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.service.BookService;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import com.floney.floney.common.exception.settlement.SettlementNotFoundException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.settlement.dto.OutcomesWithUser;
import com.floney.floney.settlement.dto.request.SettlementRequest;
import com.floney.floney.settlement.dto.response.SettlementResponse;
import com.floney.floney.settlement.entity.Settlement;
import com.floney.floney.settlement.entity.SettlementUser;
import com.floney.floney.settlement.repository.SettlementRepository;
import com.floney.floney.settlement.repository.SettlementUserRepository;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    private final BookUserRepository bookUserRepository;

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
        final Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(SettlementNotFoundException::new);
        final List<SettlementUser> settlementUsers = settlementUserRepository.findAllBySettlementAndStatus(settlement, Status.ACTIVE);
        return SettlementResponse.of(settlement, settlementUsers);
    }

    @Transactional
    public SettlementResponse create(SettlementRequest request) {
        Settlement settlement = createSettlement(request);

        validateBookUsers(request.getUserEmails(), settlement.getBook());
        List<SettlementUser> settlementUsers = createSettlementUsers(request, settlement);

        bookService.updateLastSettlementDate(
                settlement.getBook().getBookKey(),
                settlement.getCreatedAt().toLocalDate()
        );

        return SettlementResponse.of(settlement, settlementUsers);
    }

    private void validateBookUsers(final Set<String> emails, final Book book) {
        if(checkBookUsers(emails, book)) {
            throw new NotFoundBookUserException();
        }
    }

    private boolean checkBookUsers(final Set<String> emails, final Book book) {
        return emails.stream()
                .filter(email -> findBookUserByBookAndUserEmail(book, email))
                .findAny()
                .isEmpty();
    }

    private boolean findBookUserByBookAndUserEmail(final Book book, final String email) {
        return bookUserRepository.existsByBookAndUser_EmailAndStatus(book, email, Status.ACTIVE);
    }

    private Settlement createSettlement(SettlementRequest request) {
        final Book book = bookService.findBook(request.getBookKey());
        return settlementRepository.save(Settlement.of(book, request));
    }

    private List<SettlementUser> createSettlementUsers(SettlementRequest request, Settlement settlement) {
        OutcomesWithUser outcomesWithUser = createOutcomesWithUser(request);
        return createSettlementUsers(settlement, outcomesWithUser);
    }

    private List<SettlementUser> createSettlementUsers(Settlement settlement, OutcomesWithUser outcomesWithUser) {
        final Long avgOutcome = settlement.getAvgOutcome();
        List<SettlementUser> settlementUsers = new ArrayList<>();

        outcomesWithUser.getOutcomesWithUser().forEach((userEmail, outcome) -> {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(UserNotFoundException::new);
            settlementUsers.add(SettlementUser.of(settlement, user, outcome - avgOutcome));
        });

        return settlementUserRepository.saveAll(settlementUsers);
    }

    private static OutcomesWithUser createOutcomesWithUser(SettlementRequest request) {
        OutcomesWithUser outcomesWithUser = OutcomesWithUser.init(request.getUserEmails());
        outcomesWithUser.fillOutcomes(request.getOutcomes());
        return outcomesWithUser;
    }
}
