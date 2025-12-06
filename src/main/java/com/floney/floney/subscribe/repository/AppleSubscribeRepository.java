package com.floney.floney.subscribe.repository;

import com.floney.floney.common.constant.Status;
import com.floney.floney.subscribe.entity.AppleSubscribe;
import com.floney.floney.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppleSubscribeRepository extends JpaRepository<AppleSubscribe, Long> {

    Optional<AppleSubscribe> findAppleSubscribeByOriginalTransactionId(String originalTransactionId);
    
    Optional<AppleSubscribe> findAppleSubscribeByOriginalTransactionIdAndStatus(String originalTransactionId, Status status);

    Optional<AppleSubscribe> findFirstByUserOrderByUpdatedAtDesc(User user);
    
    Optional<AppleSubscribe> findFirstByUserAndStatusOrderByUpdatedAtDesc(User user, Status status);
    
    List<AppleSubscribe> findByUser(User user);

}
