package com.floney.floney.book.dto.response;

import com.floney.floney.book.domain.category.entity.Subcategory;
import lombok.*;


@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCategoryResponse {

    private String name;

    public static CreateCategoryResponse of(final Subcategory category) {
        return new CreateCategoryResponse(category.getName());
    }
}
