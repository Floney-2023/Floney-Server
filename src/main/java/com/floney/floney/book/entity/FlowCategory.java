package com.floney.floney.book.entity;

import com.floney.floney.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Entity
public class FlowCategory extends BaseEntity {
    @ManyToOne
    private Book bookId;
    private String flow;

    @Builder
    private FlowCategory(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Book bookId, String flow) {
        super(id, createdAt, updatedAt);
        this.bookId = bookId;
        this.flow = flow;
    }
}
