package com.floney.floney.book.dto.process;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DayLine {

    private Long id;

    private String content;

    private double money;

    private String categories;

    private String userEmail;

    public DayLine(Long id, String content, double money, String categories) {
        this.id = id;
        this.content = content;
        this.money = money;
        this.categories = categories;
    }

    @QueryProjection
    public DayLine(Long id, String content, double money, String categories, String userEmail) {
        this.id = id;
        this.content = content;
        this.money = money;
        this.categories = categories;
        this.userEmail = userEmail;
    }

}
