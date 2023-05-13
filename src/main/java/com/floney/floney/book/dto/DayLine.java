package com.floney.floney.book.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DayLine {
    private Long id;

    private String content;
    private Long money;
    private String categories;
    private String profileImg;

    @QueryProjection
    public DayLine(Long id, Long money, String content, String categories, String profileImg) {
        this.id = id;
        this.money = money;
        this.content = content;
        this.categories = categories;
        this.profileImg = profileImg;
    }
}
