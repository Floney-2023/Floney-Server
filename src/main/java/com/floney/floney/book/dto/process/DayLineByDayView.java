package com.floney.floney.book.dto.process;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DayLineByDayView extends DayLine{
    private String profileImg;
    private boolean exceptStatus;

    @QueryProjection
    @Builder
    public DayLineByDayView(Long id, Long money, String content, String categories, String profileImg,boolean exceptStatus) {
        super(id,content, money, categories);
        this.exceptStatus = exceptStatus;
        this.profileImg = profileImg;
    }
}
