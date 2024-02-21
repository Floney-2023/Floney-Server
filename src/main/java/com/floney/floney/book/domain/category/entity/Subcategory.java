package com.floney.floney.book.domain.category.entity;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.common.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Subcategory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @Column(nullable = false)
    private String name;

    public static Subcategory of(final Category parent, final Book book, final String name) {
        return Subcategory.builder()
            .parent(parent)
            .book(book)
            .name(name)
            .build();
    }

    public static Subcategory of(final DefaultSubcategory defaultSubcategory, final Book book) {
        return Subcategory.builder()
            .parent(defaultSubcategory.getParent())
            .book(book)
            .name(defaultSubcategory.getName())
            .build();
    }
}
