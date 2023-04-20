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
public class LineCategory extends BaseEntity {

    @ManyToOne
    private Book bookId;
    private String line;

    @Builder
    public LineCategory(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Book bookId, String line) {
        super(id, createdAt, updatedAt);
        this.bookId = bookId;
        this.line = line;
    }

}
