package com.floney.floney.book.dto.request;

import com.floney.floney.book.domain.category.CategoryType;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCategoryRequest {

    // TODO: validation 추가
    private CategoryType parent;
    private String name;
}
