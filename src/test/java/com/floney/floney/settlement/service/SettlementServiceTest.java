package com.floney.floney.settlement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import com.floney.floney.common.exception.settlement.OutcomeUserNotFoundException;
import com.floney.floney.settlement.dto.request.SettlementRequest;
import com.floney.floney.settlement.dto.response.SettlementResponse;
import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SettlementServiceTest {

    private final SettlementService settlementService;

    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final UserRepository userRepository;

    @Autowired
    public SettlementServiceTest(final SettlementService settlementService,
                                 final BookRepository bookRepository,
                                 final BookUserRepository bookUserRepository,
                                 final UserRepository userRepository) {
        this.settlementService = settlementService;
        this.bookRepository = bookRepository;
        this.bookUserRepository = bookUserRepository;
        this.userRepository = userRepository;
    }

    @Test
    @DisplayName("정산 내역을 생성하는데 성공한다")
    void createSettlement1_success() throws JsonProcessingException {
        // given
        final String json = "{\"bookKey\":\"abcdefgh\"," +
                "\"startDate\":\"2000-01-01\"," +
                "\"endDate\":\"2000-01-02\"," +
                "\"userEmails\":[\"test01@email.com\",\"test02@email.com\"]," +
                "\"outcomes\":[{" +
                "\"outcome\":10000," +
                "\"userEmail\":\"test01@email.com\"}]}";

        final SettlementRequest request = new ObjectMapper().registerModule(new JavaTimeModule())
                .readValue(json, SettlementRequest.class);

        final Book book = Book.builder()
                .bookKey(request.getBookKey())
                .code("code")
                .name("name")
                .build();

        bookRepository.save(book);

        final User user1 = User.builder()
                .email("test01@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();
        final User user2 = User.builder()
                .email("test02@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        final BookUser bookUser1 = BookUser.builder()
                .book(book)
                .user(user1)
                .build();
        final BookUser bookUser2 = BookUser.builder()
                .book(book)
                .user(user2)
                .build();

        bookUserRepository.save(bookUser1);
        bookUserRepository.save(bookUser2);

        // when
        SettlementResponse response = settlementService.create(request);

        // then
        assertThat(response.getStartDate()).isEqualTo("2000-01-01");
        assertThat(response.getEndDate()).isEqualTo("2000-01-02");
        assertThat(response.getUserCount()).isEqualTo(2);
        assertThat(response.getTotalOutcome()).isEqualTo(10000);
        assertThat(response.getOutcome()).isEqualTo(5000);
        assertThat(response.getDetails()).hasSize(2);
        assertThat(findBookByBookKey(request).getLastSettlementDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("세 명 이상의 정산 내역을 생성하는데 성공한다")
    void createSettlement2_success() throws JsonProcessingException {
        // given
        final String json = "{\"bookKey\":\"abcdefgh\"," +
                "\"startDate\":\"2000-01-01\"," +
                "\"endDate\":\"2000-01-02\"," +
                "\"userEmails\":[\"test01@email.com\",\"test02@email.com\",\"test03@email.com\"]," +
                "\"outcomes\":[" +
                "{\"outcome\":10000," +
                "\"userEmail\":\"test01@email.com\"}," +
                "{\"outcome\":20000," +
                "\"userEmail\":\"test02@email.com\"}" +
                "]}";

        final SettlementRequest request = new ObjectMapper().registerModule(new JavaTimeModule())
                .readValue(json, SettlementRequest.class);

        final Book book = Book.builder()
                .bookKey(request.getBookKey())
                .code("code")
                .name("name")
                .build();

        bookRepository.save(book);

        final User user1 = User.builder()
                .email("test01@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();
        final User user2 = User.builder()
                .email("test02@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();
        final User user3 = User.builder()
                .email("test03@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        final BookUser bookUser1 = BookUser.builder()
                .book(book)
                .user(user1)
                .build();
        final BookUser bookUser2 = BookUser.builder()
                .book(book)
                .user(user2)
                .build();
        final BookUser bookUser3 = BookUser.builder()
                .book(book)
                .user(user3)
                .build();

        bookUserRepository.save(bookUser1);
        bookUserRepository.save(bookUser2);
        bookUserRepository.save(bookUser3);

        // when
        SettlementResponse response = settlementService.create(request);

        // then
        assertThat(response.getStartDate()).isEqualTo("2000-01-01");
        assertThat(response.getEndDate()).isEqualTo("2000-01-02");
        assertThat(response.getUserCount()).isEqualTo(3);
        assertThat(response.getTotalOutcome()).isEqualTo(30000);
        assertThat(response.getOutcome()).isEqualTo(10000);
        assertThat(response.getDetails()).hasSize(3);
    }

    @Test
    @DisplayName("정산 내역을 생성하는데 실패한다 - 유저 목록에 없는 지출 내역의 유저")
    void createSettlement_fail_OutcomeUserNotFoundException() throws JsonProcessingException {
        // given
        final String json = "{\"bookKey\":\"abcdefgh\"," +
                "\"startDate\":\"2000-01-01\"," +
                "\"endDate\":\"2000-01-02\"," +
                "\"userEmails\":[\"test01@email.com\",\"test02@email.com\"]," +
                "\"outcomes\":[{" +
                "\"outcome\":10000," +
                "\"userEmail\":\"test@email.com\"}]}";

        final SettlementRequest request = new ObjectMapper().registerModule(new JavaTimeModule())
                .readValue(json, SettlementRequest.class);

        final Book book = Book.builder()
                .bookKey(request.getBookKey())
                .code("code")
                .name("name")
                .build();

        bookRepository.save(book);

        final User user1 = User.builder()
                .email("test01@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();
        final User user2 = User.builder()
                .email("test02@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        final BookUser bookUser1 = BookUser.builder()
                .book(book)
                .user(user1)
                .build();
        final BookUser bookUser2 = BookUser.builder()
                .book(book)
                .user(user2)
                .build();

        bookUserRepository.save(bookUser1);
        bookUserRepository.save(bookUser2);

        // when & then
        assertThatThrownBy(() -> settlementService.create(request))
                .isInstanceOf(OutcomeUserNotFoundException.class);
    }

    @Test
    @DisplayName("정산 내역을 생성하는데 실패한다 - 존재하지 않는 유저")
    void createSettlement_fail_UserNotFoundException() throws JsonProcessingException {
        // given
        final String json = "{\"bookKey\":\"abcdefgh\"," +
                "\"startDate\":\"2000-01-01\"," +
                "\"endDate\":\"2000-01-02\"," +
                "\"userEmails\":[\"test01@email.com\",\"test05@email.com\"]," +
                "\"outcomes\":[{" +
                "\"outcome\":10000," +
                "\"userEmail\":\"test01@email.com\"}]}";

        final SettlementRequest request = new ObjectMapper().registerModule(new JavaTimeModule())
                .readValue(json, SettlementRequest.class);

        final Book book = Book.builder()
                .bookKey(request.getBookKey())
                .code("code")
                .name("name")
                .build();

        bookRepository.save(book);

        final User user1 = User.builder()
                .email("test01@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();

        userRepository.save(user1);

        final BookUser bookUser1 = BookUser.builder()
                .book(book)
                .user(user1)
                .build();

        bookUserRepository.save(bookUser1);

        // when & then
        assertThatThrownBy(() -> settlementService.create(request))
                .isInstanceOf(NotFoundBookUserException.class);
    }

    @Test
    @DisplayName("정산 내역을 생성하는데 실패한다 - 가계부에 없는 유저")
    void createSettlement_fail_NotFoundBookUserException() throws JsonProcessingException {
        // given
        final String json = "{\"bookKey\":\"abcdefgh\"," +
                "\"startDate\":\"2000-01-01\"," +
                "\"endDate\":\"2000-01-02\"," +
                "\"userEmails\":[\"test01@email.com\",\"test02@email.com\"]," +
                "\"outcomes\":[{" +
                "\"outcome\":10000," +
                "\"userEmail\":\"test01@email.com\"}]}";

        final SettlementRequest request = new ObjectMapper().registerModule(new JavaTimeModule())
                .readValue(json, SettlementRequest.class);

        final Book book = Book.builder()
                .bookKey(request.getBookKey())
                .code("code")
                .name("name")
                .build();

        bookRepository.save(book);

        final User user1 = User.builder()
                .email("test01@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();
        final User user2 = User.builder()
                .email("test02@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        // when & then
        assertThatThrownBy(() -> settlementService.create(request))
                .isInstanceOf(NotFoundBookUserException.class);
    }

    @Test
    @DisplayName("해당 가계부의 모든 정산 내역을 불러오는데 성공한다")
    void findSettlements_success() throws JsonProcessingException {
        // given
        final String json = "{\"bookKey\":\"abcdefgh\"," +
                "\"startDate\":\"2000-01-01\"," +
                "\"endDate\":\"2000-01-02\"," +
                "\"userEmails\":[\"test01@email.com\",\"test02@email.com\"]," +
                "\"outcomes\":[{" +
                "\"outcome\":10000," +
                "\"userEmail\":\"test01@email.com\"}]}";

        final SettlementRequest request = new ObjectMapper().registerModule(new JavaTimeModule())
                .readValue(json, SettlementRequest.class);

        final Book book = Book.builder()
                .bookKey(request.getBookKey())
                .code("code")
                .name("name")
                .build();

        bookRepository.save(book);

        final User user1 = User.builder()
                .email("test01@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();
        final User user2 = User.builder()
                .email("test02@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        final BookUser bookUser1 = BookUser.builder()
                .book(book)
                .user(user1)
                .build();
        final BookUser bookUser2 = BookUser.builder()
                .book(book)
                .user(user2)
                .build();

        bookUserRepository.save(bookUser1);
        bookUserRepository.save(bookUser2);

        settlementService.create(request);

        // when
        final List<SettlementResponse> responses = settlementService.findAll(request.getBookKey());

        // then
        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("해당 정산 내역을 불러오는데 성공한다")
    void findSettlement_success() throws JsonProcessingException {
        // given
        final String json = "{\"bookKey\":\"abcdefgh\"," +
                "\"startDate\":\"2000-01-01\"," +
                "\"endDate\":\"2000-01-02\"," +
                "\"userEmails\":[\"test01@email.com\",\"test02@email.com\"]," +
                "\"outcomes\":[{" +
                "\"outcome\":10000," +
                "\"userEmail\":\"test01@email.com\"}]}";

        final SettlementRequest request = new ObjectMapper().registerModule(new JavaTimeModule())
                .readValue(json, SettlementRequest.class);

        final Book book = Book.builder()
                .bookKey(request.getBookKey())
                .code("code")
                .name("name")
                .build();

        bookRepository.save(book);

        final User user1 = User.builder()
                .email("test01@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();
        final User user2 = User.builder()
                .email("test02@email.com")
                .nickname("nickname")
                .provider(Provider.EMAIL)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        final BookUser bookUser1 = BookUser.builder()
                .book(book)
                .user(user1)
                .build();
        final BookUser bookUser2 = BookUser.builder()
                .book(book)
                .user(user2)
                .build();

        bookUserRepository.save(bookUser1);
        bookUserRepository.save(bookUser2);

        final SettlementResponse settlementResponse = settlementService.create(request);

        // when
        final SettlementResponse response = settlementService.find(settlementResponse.getId());

        // then
        assertThat(response.getId()).isEqualTo(settlementResponse.getId());
    }

    private Book findBookByBookKey(final SettlementRequest request) {
        return bookRepository.findBookByBookKeyAndStatus(request.getBookKey(), Status.ACTIVE).orElseThrow();
    }
}
