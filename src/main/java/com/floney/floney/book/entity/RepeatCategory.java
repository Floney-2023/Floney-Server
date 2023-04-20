package com.floney.floney.book.entity;

import com.floney.floney.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@RequiredArgsConstructor
public class RepeatCategory extends BaseEntity {
    @ManyToOne
    private Book bookId;
    private String kind;

    @Builder
    public RepeatCategory(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Book bookId, String kind) {
        super(id, createdAt, updatedAt);
        this.bookId = bookId;
        this.kind = kind;
    }
}
