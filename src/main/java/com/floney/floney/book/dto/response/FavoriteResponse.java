package com.floney.floney.book.dto.response;

import com.floney.floney.book.domain.favorite.Favorite;

public record FavoriteResponse(long id,
                               double money,
                               String description,
                               String lineCategoryName,
                               String lineSubcategoryName,
                               String assetSubcategoryName,
                               boolean exceptStatus) {

    public static FavoriteResponse from(final Favorite favorite) {
        return new FavoriteResponse(
            favorite.getId(),
            favorite.getMoney(),
            favorite.getDescription(),
            favorite.getLineCategory().getName().getMeaning(),
            favorite.getLineSubcategory().getName(),
            favorite.getAssetSubcategory().getName(),
            favorite.isExceptStatus()
        );
    }
}
