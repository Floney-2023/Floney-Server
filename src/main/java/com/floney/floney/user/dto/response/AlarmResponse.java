package com.floney.floney.user.dto.response;

import com.floney.floney.book.entity.Alarm;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AlarmResponse {

    private Long id;

    private String content;

    private String imgUrl;

    private LocalDateTime date;

    private boolean isReceived;

    @Builder
    private AlarmResponse(Long id, String content, String imgUrl, LocalDateTime date, boolean isReceived) {
        this.id = id;
        this.content = content;
        this.imgUrl = imgUrl;
        this.date = date;
        this.isReceived = isReceived;
    }

    public static AlarmResponse of(Alarm alarm) {
        return AlarmResponse.builder()
            .content(alarm.getContent())
            .id(alarm.getId())
            .imgUrl(alarm.getImgUrl())
            .date(alarm.getDate())
            .isReceived(alarm.getIsReceived())
            .build();

    }

}
