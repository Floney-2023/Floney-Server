package com.floney.floney.user.dto.response;

import com.floney.floney.book.domain.entity.BookLine;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MyFavoriteResponseByFlow {

    private final long bookLineId;
    private final String description;
    private final String asset;
    private final String line;
    private final double money;

    public static MyFavoriteResponseByFlow from(final BookLine bookLine) {
        return new MyFavoriteResponseByFlow(
            bookLine.getId(),
            bookLine.getDescription(),
            bookLine.getCategories().getAssetSubcategory().getName(),
            bookLine.getCategories().getLineSubcategory().getName(),
            bookLine.getMoney()
        );
    }

}
