package com.floney.floney.book.domain.category;

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
public class CustomSubCategory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @Column(nullable = false)
    private String name;

    public static CustomSubCategory of(final Category parent, final Book book, final String name) {
        return CustomSubCategory.builder()
            .parent(parent)
            .book(book)
            .name(name)
            .build();
    }
}
