package com.floney.floney.user.repository;

import com.floney.floney.common.constant.Status;
import com.floney.floney.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, Status status);

    boolean existsByProviderId(String providerId);

    Optional<User> findByProviderId(String providerId);

}
