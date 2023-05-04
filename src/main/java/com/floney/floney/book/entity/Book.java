package com.floney.floney.book.entity;

import com.floney.floney.book.dto.CreateLineRequest;
import com.floney.floney.common.BaseEntity;
import com.floney.floney.common.exception.OutOfBudgetException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Table(name = "Book", indexes = {
    @Index(name = "key", columnList = "bookKey")
})
@NoArgsConstructor
public class Book extends BaseEntity {
    private static final Long MIN_BUDGET = 0L;
    private static final Long MAX_BUDGET = 999999999L;

    @Column(nullable = false, length = 10)
    private String name;

    private String profileImg;

    private String provider;

    private String bookKey;

    private Boolean seeProfile;

    private Long initialAsset;

    private Long budget;

    private int weekStartDay;

    private Boolean carryOver;

    private String code;

    private Boolean status;


    @Builder
    private Book(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,  Boolean status,String name, String profileImg, String provider,
                 String bookKey, Boolean seeProfile, Long initialAsset, Long budget, int weekStartDay, Boolean carryOver, String code) {
        super(id, createdAt, updatedAt,status);

        this.name = name;
        this.profileImg = profileImg;
        this.provider = provider;
        this.bookKey = bookKey;
        this.seeProfile = seeProfile;
        this.initialAsset = initialAsset;
        this.budget = budget;
        this.weekStartDay = weekStartDay;
        this.carryOver = carryOver;
        this.code = code;
        this.status = status;
    }

    public void processTrans(AssetType assetType, long amount) {
        long remain = budget;
        if (assetType == AssetType.OUTCOME) {
            remain -= amount;
        } else if (assetType == AssetType.INCOME) {
            remain += amount;
        }
        isValid(remain);
        budget = remain;
    }

    private void isValid(long remain) {
        if (remain < MIN_BUDGET || remain > MAX_BUDGET) {
            throw new OutOfBudgetException();
        }
    }

}
