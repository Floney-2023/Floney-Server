package com.floney.floney.subscribe.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class GoogleCallbackDto {

    private Message message;
    private String subscription;

    @RequiredArgsConstructor
    @ToString
    @Getter

    public static class Message {
        private String data;
        private String messageId;

        private String message_id;

        private String publishTime;
        private String publish_time;

        // Getter, Setter
    }

}
