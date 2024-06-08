package com.floney.floney.book.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record FavoriteCreateRequest(
    @NotNull(message = "money를 입력해주세요") Double money,
    String description,
    @NotBlank(message = "lineCategoryName를 입력해주세요") String lineCategoryName,
    @NotBlank(message = "lineSubcategoryName를 입력해주세요") String lineSubcategoryName,
    @NotBlank(message = "assetSubcategoryName를 입력해주세요") String assetSubcategoryName,
    boolean exceptStatus) {
}
