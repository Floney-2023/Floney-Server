package com.floney.floney.book.dto.request;

import com.floney.floney.book.domain.category.CategoryType;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteCategoryRequest {

    // TODO: validation 추가
    private CategoryType parent;
    private String name;
}
