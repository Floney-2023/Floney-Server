package com.floney.floney.book.entity;

import com.floney.floney.book.dto.CreateLineRequest;
import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import static com.floney.floney.book.dto.constant.AssetType.*;
import static com.floney.floney.book.dto.constant.AssetType.find;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Table(name = "Book", indexes = {
    @Index(name = "book_keys", columnList = "bookKey")
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

    @Builder
    private Book(boolean status, String name, String profileImg, String provider,
                 String bookKey, Boolean seeProfile, Long initialAsset, Long budget,
                 int weekStartDay, Boolean carryOver, String code) {

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

    public void processTrans(CreateLineRequest request) {
        AssetType assetType = find(request.getFlow());
        Long amount = request.getMoney();
        if (assetType == OUTCOME) {
            initialAsset -= amount;
        } else if (assetType == INCOME) {
            budget += amount;
        }
    }

}
