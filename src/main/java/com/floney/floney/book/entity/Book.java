package com.floney.floney.book.entity;

import com.floney.floney.book.dto.request.CreateLineRequest;
import com.floney.floney.book.dto.request.UpdateBookImgRequest;
import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.common.exception.common.NoAuthorityException;
import java.time.LocalDate;
import java.util.Objects;

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

    private static final long DEFAULT = 0L;

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

    private Long initialAsset;

    private Long budget;

    @Column(columnDefinition = "TINYINT", length = 1)
    private Boolean carryOverStatus;

    @Column(nullable = false, length = 10)
    private String code;

    private LocalDate lastSettlementDate;

    private Long carryOverMoney;

    @Builder
    private Book(String name, String bookImg, String owner,
                 String bookKey, Boolean seeProfile, Long initialAsset, Long budget,
                 Boolean carryOverStatus, String code, Long carryOverMoney) {
        this.name = name;
        this.bookImg = bookImg;
        this.owner = owner;
        this.bookKey = bookKey;
        this.seeProfile = seeProfile;
        this.initialAsset = initialAsset;
        this.budget = budget;
        this.carryOverStatus = carryOverStatus;
        this.code = code;
        this.carryOverMoney = carryOverMoney;

    }

    public static Book initBook() {
        return new Book();
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
        this.initialAsset = asset;
    }

    public void updateBudget(Long budget) {
        this.budget = budget;
    }

    public void updateLastSettlementDate(LocalDate lastSettlementDate) {
        this.lastSettlementDate = lastSettlementDate;
    }

    public void addCarryOverMoney(CreateLineRequest request) {
        if(Objects.equals(request.getFlow(), OUTCOME.getKind())){
            carryOverMoney -= request.getMoney();
        }
        else if(Objects.equals(request.getFlow(), INCOME.getKind())){
            carryOverMoney += request.getMoney();
        }
    }

    public void initCarryOverMoney(long carryOverMoney){
        this.carryOverMoney = carryOverMoney;
    }

    public boolean getCarryOverStatus() {
        return this.carryOverStatus;
    }

    public void changeCarryOverStatus(boolean status) {
        this.carryOverStatus = status;
    }

    public void resetCarryOverMoney() {
        this.carryOverMoney = DEFAULT;
    }
}
