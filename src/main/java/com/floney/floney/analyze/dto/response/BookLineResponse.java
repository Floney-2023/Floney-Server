package com.floney.floney.analyze.dto.response;

import com.floney.floney.book.domain.entity.BookLine;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookLineResponse {
    private final double money;
    private final String asset;
    private final LocalDate lineDate;
    private final String description;
    private final String userProfileImg;

    public static BookLineResponse from(final BookLine bookLine) {
        return BookLineResponse.builder()
            .money(bookLine.getMoney())
            .asset(getCategoryKey(bookLine.getCategories().getAssetSubcategory()))
            .lineDate(bookLine.getLineDate())
            .description(bookLine.getDescription())
            .userProfileImg(bookLine.getWriter().getProfileImg())
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
