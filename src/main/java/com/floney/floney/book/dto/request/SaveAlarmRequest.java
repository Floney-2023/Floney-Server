package com.floney.floney.book.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SaveAlarmRequest {
    private String bookKey;

    private String userEmail;

    private String title;

    private String body;

    private String imgUrl;

    private LocalDateTime date;
}
