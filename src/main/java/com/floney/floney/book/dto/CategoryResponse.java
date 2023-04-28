package com.floney.floney.book.dto;

import com.floney.floney.book.entity.BookCategory;
import com.floney.floney.book.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Getter
public class CategoryResponse {

    private String name;

    @Builder
    private CategoryResponse(String name) {
        this.name = name;
    }

    public static List<CategoryResponse> to(List<Category> categories, List<BookCategory> bookCategories) {
        List<CategoryResponse> responses = categories.stream()
            .map(CategoryResponse::of)
            .collect(Collectors.toList());

        responses.addAll(bookCategories.stream()
            .map(CategoryResponse::of)
            .collect(Collectors.toList()));

        return responses;
    }

    public static CategoryResponse of(BookCategory category) {
        return CategoryResponse.builder()
            .name(category.getName())
            .build();
    }

    private static CategoryResponse of(Category category) {
        return CategoryResponse.builder()
            .name(category.getName())
            .build();
    }

}
