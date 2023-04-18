package com.floney.floney.book.entity;

import com.floney.floney.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Entity
public class FlowCategory extends BaseEntity {
    private String flow;

    @Builder
    private FlowCategory(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String flow) {
        super(id, createdAt, updatedAt);
        this.flow = flow;
    }
}
