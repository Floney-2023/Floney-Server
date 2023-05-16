package com.floney.floney.book.entity;

import com.floney.floney.book.dto.CreateLineRequest;
import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.common.BaseEntity;
import com.floney.floney.common.exception.NoAuthorityException;
import lombok.AccessLevel;
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

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Table(name = "Book", indexes = {
    @Index(name = "book_keys", columnList = "bookKey")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 300)
    private String profileImg;

    @Column(length = 20)
    private String providerEmail;

    @Column(nullable = false, length = 10)
    private String bookKey;

    @Column(columnDefinition = "TINYINT", length = 1)
    private Boolean seeProfile;

    private Long initialAsset;

    private Long budget;

    private int weekStartDay;

    @Column(columnDefinition = "TINYINT", length = 1)
    private Boolean carryOver;

    @Column(nullable = false, length = 8)
    private String code;

    @Builder
    private Book(String name, String profileImg, String providerEmail,
                 String bookKey, Boolean seeProfile, Long initialAsset, Long budget,
                 int weekStartDay, Boolean carryOver, String code) {
        this.name = name;
        this.profileImg = profileImg;
        this.providerEmail = providerEmail;
        this.bookKey = bookKey;
        this.seeProfile = seeProfile;
        this.initialAsset = initialAsset;
        this.budget = budget;
        this.weekStartDay = weekStartDay;
        this.carryOver = carryOver;
        this.code = code;

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

    public void updateName(String requestName){
        this.name = requestName;
    }

    public void isProvider(String email) {
        if(!providerEmail.equals(email)){
            throw new NoAuthorityException();
        }
    }

    public void delete() {
        this.status = false;
    }
}
