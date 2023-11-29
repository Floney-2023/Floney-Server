package com.floney.floney.user.repository;

import com.floney.floney.user.entity.SignoutOtherReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignoutOtherReasonRepository extends JpaRepository<SignoutOtherReason, Long> {
}
