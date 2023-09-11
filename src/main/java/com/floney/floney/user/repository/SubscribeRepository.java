package com.floney.floney.user.repository;

import com.floney.floney.user.entity.Subscribe;
import com.floney.floney.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    Optional<Subscribe> findSubscribeByUser(User user);
}
