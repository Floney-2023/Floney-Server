package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Getter
public class CategoryResponse {
    private String bookKey;

    private String parent;

    private String name;

    private List<CategoryResponse> children;

    @Builder
    public CategoryResponse(String bookKey, String parent, String name, List<CategoryResponse> children) {
        this.bookKey = bookKey;
        this.parent = parent;
        this.name = name;
        this.children = children;
    }


    public static CategoryResponse of(Category category) {
        return CategoryResponse.builder()
            .bookKey(category.getBook().getBookKey())
            .parent(category.getParentName())
            .name(category.getName())
            .children(category.getChildren()
                .stream()
                .map(child -> of(child))
                .collect(toList()))
            .build();
    }
}
