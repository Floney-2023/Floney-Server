package com.floney.floney.book.entity;

import com.floney.floney.common.BaseEntity;
import com.floney.floney.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@RequiredArgsConstructor
public class AssetCategory extends BaseEntity {

    @ManyToOne
    private Book bookId;

    private String asset;

    @Builder
    private AssetCategory(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Book bookId, String asset) {
        super(id, createdAt, updatedAt);
        this.bookId = bookId;
        this.asset = asset;
    }

}
