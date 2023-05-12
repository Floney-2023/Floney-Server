package com.floney.floney.book.entity;

import com.floney.floney.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BookLineCategory extends BaseEntity {
    @ManyToOne
    private BookLine bookLine;

    @ManyToOne
    private Category category;

    private String name;

    @Builder
    private BookLineCategory(BookLine bookLine, Category category, String name) {
        this.bookLine = bookLine;
        this.category = category;
        this.name = name;
    }

    public static BookLineCategory of(BookLine bookLine, Category category) {
        return BookLineCategory.builder()
            .bookLine(bookLine)
            .category(category)
            .name(category.getName())
            .build();
    }
}
