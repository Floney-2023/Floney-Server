package com.floney.floney.book.entity;

import com.floney.floney.book.dto.CategoryResponse;
import com.floney.floney.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Category extends BaseEntity {

    private static final String ROOT = "ROOT-NO-PARENT";

    private String name;

    @ManyToOne
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();


    @Builder
    private Category(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String name, Book book, Category parent) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.book = book;
        this.parent = parent;
    }

    public void addChildren(Category child) {
        this.children.add(child);
    }

    public String getParentName() {
        if (this.parent != null) {
            return this.getParent().getName();
        }
        return ROOT;
    }

    public static Category rootParent() {
        return new Category();
    }

    public boolean isNotRoot() {
        return !this.equals(rootParent());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name) && Objects.equals(book, category.book) && Objects.equals(parent, category.parent) && Objects.equals(children, category.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, book, parent, children);
    }
}
