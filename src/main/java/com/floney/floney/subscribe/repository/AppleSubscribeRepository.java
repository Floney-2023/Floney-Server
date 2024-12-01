package com.floney.floney.subscribe.repository;

import com.floney.floney.subscribe.entity.AppleSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppleSubscribeRepository extends JpaRepository<AppleSubscribe, Long> {

    Optional<AppleSubscribe> findAppleSubscribeByOriginalTransactionId(String originalTransactionId);

}
