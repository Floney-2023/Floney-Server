package com.floney.floney.user.service;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.repository.BookUserCustomRepository;
import com.floney.floney.common.dto.DelegateResponse;
import com.floney.floney.common.exception.user.NotFoundSubscribeException;
import com.floney.floney.user.dto.constant.SubscribeStatus;
import com.floney.floney.user.dto.request.SubscribeRequest;
import com.floney.floney.user.dto.response.SubscribeResponse;
import com.floney.floney.user.entity.Subscribe;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.SubscribeRepository;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscribeService {
    private final SubscribeRepository subscribeRepository;
    private final UserRepository userRepository;
    private final BookUserCustomRepository bookUserRepository;

    @Transactional
    public List<DelegateResponse> saveSubscribe(SubscribeRequest request, User user) {
        Optional<Subscribe> savedSubscribe = subscribeRepository.findSubscribeByUser(user);

        // 가계부와 유저의 구독 정보관리
        List<DelegateResponse> responses = changeUserAndBookSubscribe(request.getSubscriptionStatus(), user);

        // 기존 구독 정보 존재 시, 구독 정보 업데이트
        if (savedSubscribe.isPresent()) {
            savedSubscribe.get().update(request);
        }
        // 구독 정보 생성
        else {
            Subscribe subscribe = Subscribe.of(user, request);
            subscribeRepository.save(subscribe);
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public SubscribeResponse getSubscribe(User user) {
        Subscribe subscribe = subscribeRepository.findSubscribeByUser(user).orElseThrow(() -> new NotFoundSubscribeException(user));
        return SubscribeResponse.of(subscribe);
    }

    private List<DelegateResponse> changeUserAndBookSubscribe(String status, User user) {
        // 사용자를 구독 해지하고 가계부 위임을 처리
        if (SubscribeStatus.isExpired(status)) {
            return unsubscribeAndDelegateBooks(user);
        } else {
            // 사용자 구독상태로 변경
            user.subscribe();

            // 사용자 참여한 비활성화 가계부 모두, 구독 혜택 적용 & 방장을 사용자로
            bookUserRepository.findMyBooks(user)
                .forEach(book -> book.subscribe(user));

            userRepository.save(user);
            return Collections.emptyList();
        }
    }

    private List<DelegateResponse> unsubscribeAndDelegateBooks(User user) {
        // 사용자 구독 해지
        user.unSubscribe();

        // 사용자가 방장인 가계부 조회
        List<Book> books = bookUserRepository.findMyBooks(user);

        // 사용자의 모든 가계부 위임을 처리하고 결과를 반환
        return books.stream()
            .map(this::delegateOwner)
            .collect(Collectors.toList());
    }

    private DelegateResponse delegateOwner(Book book) {
        Optional<User> wantDelegateWhoSubscribe = bookUserRepository.findBookUserWhoSubscribe(book);

        // 구독을 한 다른 가계부 멤버가 있을 경우 위임
        if (wantDelegateWhoSubscribe.isPresent()) {
            book.delegateOwner(wantDelegateWhoSubscribe.get());
            return new DelegateResponse(true, book.getName());
        }
        // 존재하지 않을 시, 가계부 비활성화
        else {
            book.inactiveBookStatus();
            return new DelegateResponse(false, null);
        }
    }

}
