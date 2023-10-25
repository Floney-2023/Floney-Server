package com.floney.floney.book.entity;

import com.floney.floney.book.dto.constant.Currency;
import com.floney.floney.book.dto.request.UpdateBookImgRequest;
import com.floney.floney.book.event.BookDeletedEvent;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.constant.Subscribe;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.common.exception.common.NoAuthorityException;
import com.floney.floney.common.util.Events;
import com.floney.floney.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
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

    @Column(columnDefinition = "TINYINT", length = 1)
    private Boolean carryOverStatus;

    @Column(nullable = false, length = 10)
    private String code;

    private LocalDate lastSettlementDate;

    private Long carryOverMoney;

    private String currency;

    private Integer userCapacity;

    private Long asset;

    @Column
    @Enumerated(EnumType.STRING)
    private Status bookStatus;

    @Builder
    private Book(String name,
                 String profileImg,
                 String owner,
                 String bookKey,
                 boolean seeProfile,
                 boolean carryOverStatus,
                 String code,
                 Long carryOverMoney,
                 String currency,
                 Integer userCapacity,
                 Long asset,
                 Status bookStatus) {
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
        this.bookStatus = bookStatus;
    }

    public void updateName(String requestName) {
        this.name = requestName;
    }

    public void updateImg(UpdateBookImgRequest request) {
        this.bookImg = request.getNewUrl();
    }

    public void updateAsset(Long asset) {
        this.asset = asset;
    }

    public void changeSeeProfile(boolean status) {
        this.seeProfile = status;
    }

    public void updateLastSettlementDate(LocalDate lastSettlementDate) {
        this.lastSettlementDate = lastSettlementDate;
    }

    public void validateOwner(String email) {
        if (!isOwner(email)) {
            throw new NoAuthorityException(owner, email);
        }
    }

    public boolean getCarryOverStatus() {
        return this.carryOverStatus;
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
    }

    public Book subscribe(User user) {
        this.userCapacity = Subscribe.SUBSCRIBE_MAX_MEMBER.getValue();
        this.bookStatus = ACTIVE;
        this.owner = user.getEmail();
        return this;
    }

    public void delegateOwner(User user) {
        this.owner = user.getEmail();
    }

    public void inactiveBookStatus() {
        this.bookStatus = INACTIVE;
    }

    public void delete() {
        inactive();
        Events.raise(new BookDeletedEvent(getId()));
    }

    public boolean isOwner(final String email) {
        return owner.equals(email);
    }
}
