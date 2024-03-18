package com.floney.floney.book.dto.response;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.entity.RepeatBookLine;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RepeatBookLineResponse {

    private long id;

    private String description;

    private RepeatDuration repeatDuration;

    private String lineSubCategory;

    private String assetSubCategory;

    private double money;

    public RepeatBookLineResponse(final RepeatBookLine repeatBookLine) {
        this.id = repeatBookLine.getId();
        this.description = repeatBookLine.getDescription();
        this.repeatDuration = repeatBookLine.getRepeatDuration();
        this.lineSubCategory = repeatBookLine.getLineSubcategory().getName();
        this.assetSubCategory = repeatBookLine.getAssetSubcategory().getName();
        this.money = repeatBookLine.getMoney();
    }
}
