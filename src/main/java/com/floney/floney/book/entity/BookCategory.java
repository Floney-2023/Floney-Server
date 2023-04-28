package com.floney.floney.book.entity;

import com.floney.floney.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class BookCategory extends BaseEntity {
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category parent;

    @ManyToOne
    private Book book;

    @Builder
    public BookCategory(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String name, Category parent, Book book) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.parent = parent;
        this.book = book;
    }
}
