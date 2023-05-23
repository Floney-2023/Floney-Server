package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Getter
public class CategoryResponse {

    private final String name;

    @Builder
    private CategoryResponse(String name) {
        this.name = name;
    }

    public static List<CategoryResponse> to(List<Category> categories) {
        return categories.stream()
            .map(CategoryResponse::of)
            .collect(Collectors.toList());
    }

    public static CategoryResponse of(Category category) {
        return CategoryResponse.builder()
            .name(category.getName())
            .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryResponse that = (CategoryResponse) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
