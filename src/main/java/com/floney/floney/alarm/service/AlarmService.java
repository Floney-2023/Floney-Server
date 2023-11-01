package com.floney.floney.alarm.service;

import com.floney.floney.alarm.dto.response.AlarmTokenResponse;
import com.floney.floney.book.dto.request.SaveAlarmRequest;
import com.floney.floney.book.dto.request.UpdateAlarmReceived;
import com.floney.floney.user.dto.response.AlarmResponse;
import com.floney.floney.user.entity.User;

import java.util.List;

public interface AlarmService {

    AlarmTokenResponse generateToken(final User user);

    void saveAlarm(SaveAlarmRequest request);

    void updateAlarmReceived(UpdateAlarmReceived request);

    List<AlarmResponse> getAlarmByBook(String bookKey, String email);
}
