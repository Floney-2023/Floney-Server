package com.floney.floney.infra.googleAlarm.service;

import com.floney.floney.infra.googleAlarm.dto.response.GoogleAlarmTokenResponse;
import com.floney.floney.user.entity.User;

public interface GoogleAlarmService {

    GoogleAlarmTokenResponse generateToken(final User user);

}
