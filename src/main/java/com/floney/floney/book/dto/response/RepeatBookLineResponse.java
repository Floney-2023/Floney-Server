package com.floney.floney.book.dto.response;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.entity.RepeatBookLine;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RepeatBookLineResponse {

    private long id;

    private String description;

    private RepeatDuration repeatDuration;

    private String lineSubCategory;

    private String assetSubCategory;

    private double money;

    private String memo;

    private List<BookLineImgResponse> imageUrls;

    public RepeatBookLineResponse(final RepeatBookLine repeatBookLine,final List<BookLineImgResponse> imageUrls) {
        this.id = repeatBookLine.getId();
        this.description = repeatBookLine.getDescription();
        this.repeatDuration = repeatBookLine.getRepeatDuration();
        this.lineSubCategory = repeatBookLine.getLineSubcategory().getName();
        this.assetSubCategory = repeatBookLine.getAssetSubcategory().getName();
        this.money = repeatBookLine.getMoney();
        this.memo = repeatBookLine.getMemo();
        this.imageUrls = imageUrls;
    }
}
