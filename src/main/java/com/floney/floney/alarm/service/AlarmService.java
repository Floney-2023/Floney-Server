package com.floney.floney.alarm.service;

import com.floney.floney.alarm.dto.response.AlarmTokenResponse;
import com.floney.floney.user.entity.User;

public interface AlarmService {

    AlarmTokenResponse generateToken(final User user);
}
