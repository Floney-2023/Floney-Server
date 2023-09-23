package com.floney.floney.book.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SaveAlarmRequest {
    private String bookKey;
    private String content;
    private String imgUrl;
    private LocalDate date;
}
