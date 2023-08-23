package com.floney.floney.book.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ChangeBookLineRequest {
    private Long lineId;

    private String bookKey;

    private Long money;

    private String flow;

    private String asset;

    private String line;

    private LocalDate lineDate;

    private String description;

    private Boolean except;

}
