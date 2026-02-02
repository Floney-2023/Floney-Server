package com.floney.floney.book.dto.response;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.entity.BookLine;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookLineResponse {

    private final long id;
    private final double money;
    private final CategoryType lineType;
    private final String asset;
    private final String line;
    private final LocalDate lineDate;
    private final String description;
    private final Boolean except;
    private final String nickname;
    private final RepeatDuration repeatDuration;
    private final String memo;
    private final List<String> imageUrls;

    public static BookLineResponse from(final BookLine bookLine,final List<String> imgUrls) {
        return BookLineResponse.builder()
            .id(bookLine.getId())
            .money(bookLine.getMoney())
            .lineType(bookLine.getCategories().getLineCategory().getName())
            .asset(getCategoryKey(bookLine.getCategories().getAssetSubcategory()))
            .line(getCategoryKey(bookLine.getCategories().getLineSubcategory()))
            .lineDate(bookLine.getLineDate())
            .description(bookLine.getDescription())
            .except(bookLine.getExceptStatus())
            .nickname(bookLine.getWriterNickName())
            .repeatDuration(bookLine.getRepeatDuration())
            .memo(bookLine.getMemo() != null ? bookLine.getMemo() : "")
            .imageUrls(imgUrls)
            .build();
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
