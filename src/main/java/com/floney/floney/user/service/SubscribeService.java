package com.floney.floney.user.service;

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

@Service
@RequiredArgsConstructor
public class SubscribeService {
    private final SubscribeRepository subscribeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveSubscribe(SubscribeRequest request, User user) {
        changeUserSubscribe(request.getSubscriptionStatus(), user);
        Subscribe subscribe = Subscribe.of(user, request);
        subscribeRepository.save(subscribe);
    }

    @Transactional
    public void updateSubscribe(SubscribeRequest request, User user) {
        changeUserSubscribe(request.getSubscriptionStatus(), user);
        Subscribe subscribe = subscribeRepository.findSubscribeByUser(user);
        subscribe.update(request);
    }

    @Transactional(readOnly = true)
    public SubscribeResponse getSubscribe(User user) {
        Subscribe subscribe = subscribeRepository.findSubscribeByUser(user);
        return SubscribeResponse.of(subscribe);
    }

    private void changeUserSubscribe(String status, User user) {
        if (SubscribeStatus.isExpired(status)) {
            user.unSubscribe();
        } else {
            user.subscribe();
            //구독자가 owner인 가계부 정원 4명으로 늘리기
        }
        userRepository.save(user);
    }


    //구독 해지 넘기기 랜덤


}
