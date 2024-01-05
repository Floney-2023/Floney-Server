package com.floney.floney.book.dto.process;

import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.vo.CategoryType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class DayLineResponse {
    private long id;
    private double money;
    private String description;
    private String userNickName;
    private String profileImg;
    private String flowCategory;
    private String lineCategory;
    private String assetCategory;
    private boolean exceptStatus;

    @QueryProjection
    public DayLineResponse(final long id, final BookLine bookLine, final String userNickName, final String profileImg) {
        this.id = id;
        this.money = bookLine.getMoney();
        this.description = bookLine.getDescription();
        this.userNickName = userNickName;
        this.profileImg = profileImg;
        this.flowCategory = bookLine.getBookLineCategories().get(CategoryType.FLOW).getName();
        this.lineCategory = bookLine.getBookLineCategories().get(CategoryType.FLOW_LINE).getName();
        this.assetCategory = bookLine.getBookLineCategories().get(CategoryType.ASSET).getName();
        this.exceptStatus = bookLine.getExceptStatus();
    }


}
