package com.floney.floney.book.entity;

import com.floney.floney.book.dto.request.CreateLineRequest;
import com.floney.floney.book.dto.request.UpdateBookImgRequest;
import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.common.exception.common.NoAuthorityException;
import java.time.LocalDate;
import lombok.*;
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
    private String bookImg;

    @Column(length = 20)
    private String owner;

    @Column(nullable = false, length = 10)
    private String bookKey;

    @Column(columnDefinition = "TINYINT", length = 1)
    private Boolean seeProfile;

    private Long initAsset;

    private Long initBudget;

    @Column(columnDefinition = "TINYINT", length = 1)
    private Boolean carryOver;

    @Column(nullable = false, length = 10)
    private String code;

    private LocalDate lastSettlementDate;

    @Builder
    private Book(String name, String bookImg, String owner,
                 String bookKey, Boolean seeProfile, Long initAsset, Long initBudget,
                 Boolean carryOver, String code) {
        this.name = name;
        this.bookImg = bookImg;
        this.owner = owner;
        this.bookKey = bookKey;
        this.seeProfile = seeProfile;
        this.initAsset = initAsset;
        this.initBudget = initBudget;
        this.carryOver = carryOver;
        this.code = code;

    }

    public static Book initBook() {
        return new Book();
    }

    public void updateName(String requestName) {
        this.name = requestName;
    }

    public void isOwner(String email) {
        if (!owner.equals(email)) {
            throw new NoAuthorityException();
        }
    }

    public void updateImg(UpdateBookImgRequest request) {
        this.bookImg = request.getNewUrl();
    }

    public void changeSeeProfile(boolean status) {
        this.seeProfile = status;
    }

    public void updateAsset(Long asset) {
        this.initAsset = asset;
    }

    public void updateBudget(Long budget) {
        this.initBudget = budget;
    }

    public void updateLastSettlementDate(LocalDate lastSettlementDate) {
        this.lastSettlementDate = lastSettlementDate;
    }
}
