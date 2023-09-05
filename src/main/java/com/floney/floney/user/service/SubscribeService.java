package com.floney.floney.user.service;

import com.floney.floney.user.dto.request.SubscribeRequest;
import com.floney.floney.user.dto.response.SubscribeResponse;
import com.floney.floney.user.entity.Subscribe;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscribeService {
    private final SubscribeRepository subscribeRepository;

    @Transactional
    public void saveSubscribe(SubscribeRequest request, User user) {
        user.subscribe();

        Subscribe subscribe = Subscribe.of(user, request);
        subscribeRepository.save(subscribe);
    }

    @Transactional
    public void updateSubscribe(SubscribeRequest request, User user){
        Subscribe subscribe = subscribeRepository.findSubscribeByUser(user);
        subscribe.update(request);
    }

    @Transactional(readOnly = true)
    public SubscribeResponse getSubscribe(User user) {
        Subscribe subscribe = subscribeRepository.findSubscribeByUser(user);
        return SubscribeResponse.of(subscribe);
    }
}
