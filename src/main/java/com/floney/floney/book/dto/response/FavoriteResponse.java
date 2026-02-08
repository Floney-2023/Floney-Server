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
            getCategoryKey(favorite.getLineSubcategory()),
            getCategoryKey(favorite.getAssetSubcategory()),
            favorite.isExceptStatus()
        );
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
