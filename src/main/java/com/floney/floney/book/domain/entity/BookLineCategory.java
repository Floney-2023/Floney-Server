package com.floney.floney.book.domain.entity;

import com.floney.floney.book.domain.entity.category.Category;
import com.floney.floney.common.entity.BaseEntity;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BookLineCategory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private BookLine bookLine;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Column(nullable = false)
    private String name;

    @Builder
    @QueryProjection
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
