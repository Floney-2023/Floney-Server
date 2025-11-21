package com.floney.floney.subscribe.repository;

import com.floney.floney.subscribe.entity.AppleSubscribe;
import com.floney.floney.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppleSubscribeRepository extends JpaRepository<AppleSubscribe, Long> {

    Optional<AppleSubscribe> findAppleSubscribeByOriginalTransactionId(String originalTransactionId);

    Optional<AppleSubscribe> findFirstByUserOrderByUpdatedAtDesc(User user);

}
