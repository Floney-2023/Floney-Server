package com.floney.floney.alarm.service;

import com.floney.floney.alarm.client.GoogleTokenProxy;
import com.floney.floney.alarm.dto.response.AlarmTokenResponse;
import com.floney.floney.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {

    private final GoogleTokenProxy accessGoogleTokenProxy;

    @Override
    public AlarmTokenResponse generateToken(final User user) {
        validateUserActive(user);
        return new AlarmTokenResponse(accessGoogleTokenProxy.generate());
    }

    private static void validateUserActive(final User user) {
        if (user.isInactive()) {
            throw new RuntimeException();
        }
    }
}
