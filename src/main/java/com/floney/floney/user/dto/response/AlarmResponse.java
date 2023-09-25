package com.floney.floney.user.dto.response;

import com.floney.floney.book.entity.Alarm;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AlarmResponse {

    private Long id;

    private String title;

    private String body;

    private String imgUrl;

    private LocalDateTime date;

    private boolean isReceived;

    @Builder
    private AlarmResponse(Long id, String title, String body, String imgUrl, LocalDateTime date, boolean isReceived) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.imgUrl = imgUrl;
        this.date = date;
        this.isReceived = isReceived;
    }

    public static AlarmResponse of(Alarm alarm) {
        return AlarmResponse.builder()
            .title(alarm.getTitle())
            .body(alarm.getBody())
            .id(alarm.getId())
            .imgUrl(alarm.getImgUrl())
            .date(alarm.getDate())
            .isReceived(alarm.getIsReceived())
            .build();

    }

}
