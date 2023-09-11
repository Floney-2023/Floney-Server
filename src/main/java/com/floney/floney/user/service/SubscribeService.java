package com.floney.floney.user.service;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.repository.BookRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.floney.floney.common.constant.Subscribe.SUBSCRIBE_MAX_BOOK;

@Service
@RequiredArgsConstructor
public class SubscribeService {
    private final SubscribeRepository subscribeRepository;
    private final UserRepository userRepository;
    private final BookUserCustomRepository bookUserRepository;
    private final BookRepository bookRepository;

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
        Subscribe subscribe = subscribeRepository.findSubscribeByUser(user)
            .orElseThrow(() -> new NotFoundSubscribeException(user.getEmail()));
        return SubscribeResponse.of(subscribe);
    }

    private List<DelegateResponse> changeUserAndBookSubscribe(String status, User user) {
        // 사용자 구독 해지
        if (SubscribeStatus.isExpired(status)) {
            return unsubscribeAndDelegateBooks(user);
        } else {
            // 사용자 구독
            user.subscribe();
            userRepository.save(user);

            // 사용자 참여한 비활성화 가계부 모두, 구독 혜택 적용 & 방장을 사용자로
            return bookUserRepository.findMyInactiveBooks(user)
                .stream()
                .map((book) -> book.subscribe(user))
                .map(bookRepository::save)
                .map(book -> DelegateResponse.of(book,user))
                .collect(Collectors.toList());
        }
    }

    private List<DelegateResponse> unsubscribeAndDelegateBooks(User user) {
        // 사용자 구독 해지
        user.unSubscribe();
        userRepository.save(user);

        // 사용자가 방장인 가계부 조회
        List<Book> books = bookUserRepository.findBookByOwner(user);

        // 사용자의 구독 혜택을 받는 중인 가계부 위임을 처리하고 결과를 반환
        return books.stream()
            .filter(this::isOverSubscribeLimit)
            .map(this::delegateOwner)
            .collect(Collectors.toList());
    }

    // 구독 혜택을 받는 가게부(가계부원 2명 이상)
    private boolean isOverSubscribeLimit(Book book) {
        long count = bookUserRepository.countBookUser(book);
        return count > SUBSCRIBE_MAX_BOOK.getValue();
    }

    private DelegateResponse delegateOwner(Book book) {
        // 구독을 한 다른 멤버 조회
        Optional<User> wantDelegateWhoSubscribe = bookUserRepository.findBookUserWhoSubscribe(book);

        // 존재할 경우 방장 위임
        if (wantDelegateWhoSubscribe.isPresent()) {
            User delegateTarget = wantDelegateWhoSubscribe.get();
            book.delegateOwner(delegateTarget);
            bookRepository.save(book);
            return DelegateResponse.of(book,delegateTarget);
        }
        // 미존재시, 가계부 비활성화
        else {
            book.inactiveBookStatus();
            bookRepository.save(book);
            return DelegateResponse.of(book,null);
        }

    }

}
