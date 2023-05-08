package com.floney.floney.book.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class MyBookInfo {

    private String bookImg;

    private String name;

    private Long memberCount;

    @QueryProjection
    @Builder
    public MyBookInfo(String bookImg, String name, Long memberCount) {
        this.bookImg = bookImg;
        this.name = name;
        this.memberCount = memberCount;
    }


}
