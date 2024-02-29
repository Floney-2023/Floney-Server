package com.floney.floney.book.dto.response;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.entity.RepeatBookLine;
import lombok.Getter;

@Getter
public class RepeatBookLineResponse {

    private final long id;

    private final String description;

    private final RepeatDuration repeatDuration;

    private final String lineSubCategory;

    private final String assetSubCategory;

    private final double money;

    public RepeatBookLineResponse(final RepeatBookLine repeatBookLine) {
        this.id = repeatBookLine.getId();
        this.description = repeatBookLine.getDescription();
        this.repeatDuration = repeatBookLine.getRepeatDuration();
        this.lineSubCategory = repeatBookLine.getLineSubcategory().getName();
        this.assetSubCategory = repeatBookLine.getAssetSubcategory().getName();
        this.money = repeatBookLine.getMoney();
    }
}
