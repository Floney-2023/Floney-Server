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
        this.lineSubCategory = getCategoryKey(repeatBookLine.getLineSubcategory());
        this.assetSubCategory = getCategoryKey(repeatBookLine.getAssetSubcategory());
        this.money = repeatBookLine.getMoney();
        this.memo = repeatBookLine.getMemo() != null ? repeatBookLine.getMemo() : "";
        this.imageUrls = imageUrls;
    }

    /**
     * Returns categoryKey if available (for default categories),
     * otherwise returns name (for user-defined categories)
     */
    private static String getCategoryKey(final com.floney.floney.book.domain.category.entity.Subcategory subcategory) {
        String categoryKey = subcategory.getCategoryKey();
        return categoryKey != null ? categoryKey : subcategory.getName();
    }
}
