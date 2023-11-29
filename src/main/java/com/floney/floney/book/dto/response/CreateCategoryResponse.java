package com.floney.floney.book.dto.response;

import com.floney.floney.book.domain.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;


@Getter
public class CreateCategoryResponse {

    private final String name;

    @Builder
    private CreateCategoryResponse(String name) {
        this.name = name;
    }

    public static CreateCategoryResponse of(Category category) {
        return CreateCategoryResponse.builder()
                .name(category.getName())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateCategoryResponse that = (CreateCategoryResponse) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
