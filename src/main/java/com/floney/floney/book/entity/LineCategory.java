package com.floney.floney.book.entity;

import com.floney.floney.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Getter
@RequiredArgsConstructor
public class LineCategory extends BaseEntity {
    private String line;

    @Builder
    public LineCategory(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String line) {
        super(id, createdAt, updatedAt);
        this.line = line;
    }

}
