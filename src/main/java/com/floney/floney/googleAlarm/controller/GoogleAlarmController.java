package com.floney.floney.googleAlarm.controller;

import com.floney.floney.googleAlarm.service.GoogleAlarmService;
import com.floney.floney.book.dto.request.SaveAlarmRequest;
import com.floney.floney.book.dto.request.UpdateAlarmReceived;
import com.floney.floney.user.dto.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alarm")
@RequiredArgsConstructor
public class GoogleAlarmController {

    private final GoogleAlarmService alarmService;

    @GetMapping("/tokens")
    public ResponseEntity<?> generateToken(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(alarmService.generateToken(userDetails.getUser()));
    }

    /**
     * 가계부 알람 조회
     *
     * @param bookKey     가계부 키
     * @param userDetails 유저 정보
     * @return List<AlarmResponse> 알람 정보
     */
    @GetMapping()
    public ResponseEntity<?> getAlarm(@RequestParam String bookKey, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(alarmService.getAlarmByBook(bookKey, userDetails.getUsername()), HttpStatus.OK);
    }

    /**
     * 알람 저장
     *
     * @body SaveAlarmRequest 알람 저장 정보
     */
    @PostMapping()
    public ResponseEntity<?> saveAlarm(@RequestBody SaveAlarmRequest request) {
        alarmService.saveAlarm(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 알람 읽음 처리
     *
     * @body UpdateAlarmReceived 알람 읽음 상태
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateAlarm(@RequestBody UpdateAlarmReceived request) {
        alarmService.updateAlarmReceived(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
