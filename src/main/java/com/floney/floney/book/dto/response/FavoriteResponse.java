package com.floney.floney.book.dto.response;

public record FavoriteResponse(long id,
                               double money,
                               String description,
                               String lineCategoryName,
                               String lineSubcategoryName,
                               String assetSubcategoryName) {
}
