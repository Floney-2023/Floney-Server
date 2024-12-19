package com.floney.floney.subscribe.repository;

import com.floney.floney.subscribe.entity.AndroidSubscribe;
import com.floney.floney.subscribe.entity.AppleSubscribe;
import com.floney.floney.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AndroidSubscribeRepository extends JpaRepository<AndroidSubscribe, Long> {

    Optional<AndroidSubscribe> findAndroidSubscribeByOrderId(String orderId);

    Optional<AndroidSubscribe> findAndroidSubscribeByUserOrderByUpdatedAtDesc(User user);

}
