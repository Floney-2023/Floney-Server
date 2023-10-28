package com.floney.floney.user.service;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.dto.request.SubscribeRequest;
import com.floney.floney.user.entity.Subscribe;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.SubscribeRepository;
import com.floney.floney.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SubscribeServiceTest {

    @Mock
    private SubscribeRepository subscribeRepository;

    @Mock
    private BookUserRepository bookUserRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscribeService subscribeService;

    private Subscribe subscribe;

    private User user;

    @BeforeEach
    void init() {
        user = UserFixture.createUser();
        SubscribeRequest savedRequest = SubscribeRequest.builder()
            .subscriptionStatus("active")
            .build();
        subscribe = Subscribe.of(user, savedRequest);
    }

    @Test
    @DisplayName("기존에 저장된 값이 없을 경우 새로 구독을 생성한다")
    void create_subscribe() {
        given(subscribeRepository.findSubscribeByUser(any(User.class)))
            .willReturn(Optional.empty());

        SubscribeRequest request = SubscribeRequest.builder()
            .subscriptionStatus("active")
            .build();
        User user = UserFixture.createUser();

        Assertions.assertThat(subscribeService.saveSubscribe(request, user))
            .isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("구독 정보를 변경하면 기존에 저장된 값을 변경한다")
    void update_subscribe() {
        given(subscribeRepository.findSubscribeByUser(any(User.class)))
            .willReturn(Optional.ofNullable(subscribe));

        SubscribeRequest newRequest = SubscribeRequest.builder()
            .subscriptionStatus("active")
            .build();

        subscribeService.saveSubscribe(newRequest, user);
        Assertions.assertThat(subscribe.getSubscriptionStatus())
            .isEqualTo("active");
    }

    @Test
    @DisplayName("구독을 해지한다 - 팀원인 경우 유저의 구독 정보가 false로 변경된다")
    void un_subscribe() {
        given(subscribeRepository.findSubscribeByUser(any(User.class)))
            .willReturn(Optional.ofNullable(subscribe));

        // 본인이 방장인 가계부가 존재하지 않는다.
        given(bookUserRepository.findBookByOwner(any(User.class)))
            .willReturn(Collections.emptyList());

        SubscribeRequest newRequest = SubscribeRequest.builder()
            .subscriptionStatus("expired")
            .build();

        subscribeService.saveSubscribe(newRequest, user);

        Assertions.assertThat(user.isSubscribe())
            .isFalse();
    }

    @Test
    @DisplayName("구독을 해지한다 - 방장인 경우 + 위임할 가계부원이 없을 경우 가계부는 비활성화된다")
    void un_subscribe_owner_no_delegate() {
        given(subscribeRepository.findSubscribeByUser(any(User.class)))
            .willReturn(Optional.ofNullable(subscribe));

        // 본인이 방장인 가계부가 존재한다
        Book book = BookFixture.createBook();
        given(bookUserRepository.findBookByOwner(any(User.class)))
            .willReturn(Collections.singletonList(book));

        // 해당 가계부는 구독 혜택을 받는 중
        given(bookUserRepository.countByBookExclusively(any(Book.class)))
            .willReturn(4);

        // 위임 할 가계부원이 없을 경우
        given(bookUserRepository.findBookUserWhoSubscribeExclusively(any(Book.class)))
            .willReturn(Optional.empty());

        SubscribeRequest newRequest = SubscribeRequest.builder()
            .subscriptionStatus("expired")
            .build();

        subscribeService.saveSubscribe(newRequest, user);

        // 가계부는 비활성화
        Assertions.assertThat(book.getBookStatus())
            .isEqualTo(Status.INACTIVE);
    }

    @Test
    @DisplayName("구독을 해지한다 - 방장인 경우 + 위임할 가계부원이 있을 경우 가계부 방장이 위임된다")
    void un_subscribe_owner_delegate() {
        given(subscribeRepository.findSubscribeByUser(any(User.class)))
            .willReturn(Optional.ofNullable(subscribe));

        // 본인이 방장인 가계부가 존재한다
        Book book = BookFixture.createBook();
        given(bookUserRepository.findBookByOwner(any(User.class)))
            .willReturn(Collections.singletonList(book));

        // 해당 가계부는 구독 혜택을 받는 중
        given(bookUserRepository.countByBookExclusively(any(Book.class)))
            .willReturn(4);

        // 위임 할 가계부원이 존재할 경우
        User delegateUser = UserFixture.createUser2();
        given(bookUserRepository.findBookUserWhoSubscribeExclusively(any(Book.class)))
            .willReturn(Optional.of(delegateUser));

        SubscribeRequest newRequest = SubscribeRequest.builder()
            .subscriptionStatus("expired")
            .build();

        subscribeService.saveSubscribe(newRequest, user);

        // 방장이 위임된다
        Assertions.assertThat(book.getOwner())
            .isEqualTo(delegateUser.getEmail());
    }
}
