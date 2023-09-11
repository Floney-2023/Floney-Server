package com.floney.floney.book.dto.process;

import com.floney.floney.common.constant.Status;
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

    private String bookKey;

    private String name;

    private Long memberCount;

    private Status bookStatus;

    @QueryProjection
    @Builder
    public MyBookInfo(String bookImg, String name, Long memberCount, String bookKey, Status bookStatus) {
        this.bookImg = bookImg;
        this.name = name;
        this.bookKey = bookKey;
        this.memberCount = memberCount;
        this.bookStatus = bookStatus;
    }
}
