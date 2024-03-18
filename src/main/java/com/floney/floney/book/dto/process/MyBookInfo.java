package com.floney.floney.book.dto.process;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyBookInfo {

    private String bookImg;
    private String bookKey;
    private String name;
    private Long memberCount;

    @Builder
    @QueryProjection
    public MyBookInfo(final String bookImg,
                      final String bookKey,
                      final String name,
                      final Long memberCount) {
        this.bookImg = bookImg;
        this.bookKey = bookKey;
        this.name = name;
        this.memberCount = memberCount;
    }
}
