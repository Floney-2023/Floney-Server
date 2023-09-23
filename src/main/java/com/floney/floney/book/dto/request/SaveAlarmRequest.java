package com.floney.floney.book.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SaveAlarmRequest {
    private String bookKey;
    private String content;
    private String imgUrl;
    private LocalDateTime date;
}
