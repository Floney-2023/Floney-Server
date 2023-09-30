package com.floney.floney.user.repository;

import com.floney.floney.user.dto.constant.SignoutType;

public interface SignoutReasonCustomRepository {

    void increaseCount(final SignoutType signoutType);
}
