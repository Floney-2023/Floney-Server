package com.floney.floney.infra.alarm.service;

import com.floney.floney.book.dto.request.SaveAlarmRequest;
import com.floney.floney.book.dto.request.UpdateAlarmReceived;
import com.floney.floney.user.dto.response.AlarmResponse;

import java.util.List;

public interface BookAlarmService {
    void saveAlarm(SaveAlarmRequest request);

    void updateAlarmReceived(UpdateAlarmReceived request);

    List<AlarmResponse> getAlarmByBook(String bookKey, String email);
}
