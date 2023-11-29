package com.floney.floney.infra.googleAlarm.service;

import com.floney.floney.infra.googleAlarm.client.GoogleTokenProxy;
import com.floney.floney.infra.googleAlarm.dto.response.GoogleAlarmTokenResponse;
import com.floney.floney.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleAlarmServiceImpl implements GoogleAlarmService {

    private final GoogleTokenProxy accessGoogleTokenProxy;

    private static void validateUserActive(final User user) {
        if (user.isInactive()) {
            throw new RuntimeException();
        }
    }

    @Override
    public GoogleAlarmTokenResponse generateToken(final User user) {
        validateUserActive(user);
        return new GoogleAlarmTokenResponse(accessGoogleTokenProxy.generate());
    }

}
