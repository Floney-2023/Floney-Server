package com.floney.floney.user.repository;

import com.floney.floney.user.dto.constant.SignoutType;
import com.floney.floney.user.entity.SignoutReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignoutReasonRepository extends JpaRepository<SignoutReason, Long>, SignoutReasonCustomRepository {

    Optional<SignoutReason> findByReasonType(final SignoutType reasonType);
}
