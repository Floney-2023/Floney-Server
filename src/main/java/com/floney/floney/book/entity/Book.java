package com.floney.floney.book.entity;

import com.floney.floney.book.dto.constant.Currency;
import com.floney.floney.book.dto.request.UpdateBookImgRequest;
import com.floney.floney.book.event.BookDeletedEvent;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.common.exception.book.MaxMemberException;
import com.floney.floney.common.exception.common.NoAuthorityException;
import com.floney.floney.common.util.Events;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {

    private static final double DEFAULT = 0.0;

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

    @Column(columnDefinition = "TINYINT", length = 1)
    private Boolean carryOverStatus;

    @Column(nullable = false, length = 10)
    private String code;

    private LocalDate lastSettlementDate;

    private Double carryOverMoney;

    private String currency;

    private Integer userCapacity;

    private Double asset;

    @Builder
    private Book(String name,
                 String profileImg,
                 String owner,
                 String bookKey,
                 boolean seeProfile,
                 boolean carryOverStatus,
                 String code,
                 double carryOverMoney,
                 String currency,
                 Integer userCapacity,
                 double asset) {
        this.name = name;
        this.bookImg = profileImg;
        this.owner = owner;
        this.bookKey = bookKey;
        this.seeProfile = seeProfile;
        this.carryOverStatus = carryOverStatus;
        this.code = code;
        this.carryOverMoney = carryOverMoney;
        this.currency = currency;
        this.userCapacity = userCapacity;
        this.asset = asset;
    }

    public void updateName(String requestName) {
        this.name = requestName;
    }

    public void validateOwner(String email) {
        if (!owner.equals(email)) {
            throw new NoAuthorityException(owner, email);
        }
    }

    public void updateImg(UpdateBookImgRequest request) {
        this.bookImg = request.getNewUrl();
    }

    public void updateAsset(double asset) {
        this.asset = asset;
    }

    public void changeSeeProfile(boolean status) {
        this.seeProfile = status;
    }

    public void updateLastSettlementDate(LocalDate lastSettlementDate) {
        this.lastSettlementDate = lastSettlementDate;
    }

    public void changeCarryOverStatus(boolean status) {
        this.carryOverStatus = status;
    }

    public void changeCurrency(Currency requestCurrency) {
        this.currency = requestCurrency.toString();
    }

    public void initBook() {
        this.asset = DEFAULT;
        this.carryOverStatus = Boolean.FALSE;
        this.lastSettlementDate = null;
    }

    public void validateCanJoinMember(final int memberCount) {
        // TODO: memberCount > userCapacity인 경우는 서버 에러로 변경
        if (memberCount > userCapacity) {
            throw new MaxMemberException(bookKey, memberCount);
        }
    }

    public void delete() {
        inactive();
        Events.raise(new BookDeletedEvent(getId()));
    }

    public boolean isOwner(final String email) {
        return owner.equals(email);
    }
}
