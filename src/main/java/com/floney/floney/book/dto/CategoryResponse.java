package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Getter
public class CategoryResponse {
    private static final String ROOT_BOOK = "ROOT";

    private String name;

    private List<CategoryResponse> children;

    @Builder
    private CategoryResponse(String name, List<CategoryResponse> children) {
        this.name = name;
        this.children = children;
    }

    public static CategoryResponse of(Category category,String bookKey) {
        return CategoryResponse.builder()
            .name(category.getName())
            .children(children(category.getChildren(),bookKey))
            .build();
    }

    private static List<CategoryResponse> children(List<Category> children,String bookKey){
        return children
            .stream()
            .filter(child -> child.getBook()
                .getBookKey()
                .equals(bookKey))
            .filter(child -> child.getBook()
                .getName()
                .equals(ROOT_BOOK))
            .map(child -> of(child,bookKey))
            .collect(toList());
    }

}
