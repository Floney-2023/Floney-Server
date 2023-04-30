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
public class RepeatCategory extends BaseEntity {

    private String kind;

    @Builder
    public RepeatCategory(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String kind) {
        super(id, createdAt, updatedAt);
        this.kind = kind;
    }
}
