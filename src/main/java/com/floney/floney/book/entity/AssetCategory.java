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
public class AssetCategory extends BaseEntity {

    private String asset;

    @Builder
    private AssetCategory(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String asset) {
        super(id, createdAt, updatedAt);
        this.asset = asset;
    }

}
