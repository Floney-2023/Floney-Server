package com.floney.floney.book.entity;

import com.floney.floney.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {

    @Column(nullable = false, length = 10)
    private String name;

    private String profileImg;

    private String provider;

    @Column(columnDefinition = "BINARY(16)")
    private UUID bookKey;

    private Boolean seeProfile;

    private Long initialAsset;

    private Long budget;

    private int weekStartDay;

    private Boolean carryOver;

    private String code;

    private Boolean status;


    @Builder
    private Book(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String name, String profileImg, String provider,
                 UUID bookKey, Boolean seeProfile, Long initialAsset, Long budget, int weekStartDay, Boolean carryOver, String code, Boolean status) {
        super(id, createdAt, updatedAt);

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

    public String getBookKey() {
        return this.bookKey.toString();
    }

}
