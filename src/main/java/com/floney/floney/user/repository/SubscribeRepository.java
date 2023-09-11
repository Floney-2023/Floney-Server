package com.floney.floney.user.repository;

import com.floney.floney.user.entity.Subscribe;
import com.floney.floney.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    Subscribe findSubscribeByUser(User user);
}
