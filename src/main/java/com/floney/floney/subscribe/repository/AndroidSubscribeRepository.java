package com.floney.floney.subscribe.repository;

import com.floney.floney.common.constant.Status;
import com.floney.floney.subscribe.entity.AndroidSubscribe;
import com.floney.floney.subscribe.entity.AppleSubscribe;
import com.floney.floney.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AndroidSubscribeRepository extends JpaRepository<AndroidSubscribe, Long> {

    Optional<AndroidSubscribe> findFirstAndroidSubscribeByUserOrderByUpdatedAtDesc(User user);
    
    Optional<AndroidSubscribe> findFirstAndroidSubscribeByUserAndStatusOrderByUpdatedAtDesc(User user, Status status);
    
    Optional<AndroidSubscribe> findAndroidSubscribeByOrderIdAndStatus(String orderId, Status status);
    
    List<AndroidSubscribe> findByUser(User user);

}
