package com.floney.floney.book.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class DayLineByDayView extends DayLine{
    private String profileImg;

    @QueryProjection
    @Builder
    public DayLineByDayView(Long id, Long money, String content, String categories, String profileImg) {
        super(id,content, money, categories);
        this.profileImg = profileImg;
    }
}
