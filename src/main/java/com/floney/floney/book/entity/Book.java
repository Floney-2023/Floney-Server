package com.floney.floney.book.entity;

import com.floney.floney.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {

    @Column(nullable = false, length = 10)
    private String name;

    @Column(name = "profile_img", length = 300)
    private String profileImg;

    @Column(length = 20, nullable = false)
    private String provider;

    @Column(columnDefinition = "BINARY(16)")
    private UUID bookKey;

    @Column(name = "see_profile", nullable = false)
    private Boolean seeProfile;

    @Column(name = "initial_asset", nullable = false)
    private Long initialAsset;

    @Column(nullable = false)
    private Long budget;

    @Column(name = "week_start_day", nullable = false)
    private int weekStartDay;

    @Column(name = "carry_over", nullable = false)
    private Boolean carryOver;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Boolean status;

    @PrePersist
    public void init() {
        if (this.bookKey == null) {
            this.bookKey = UUID.randomUUID();
        }

        if(this.status == null){
            this.status = Boolean.TRUE;
        }

        if(this.budget == null && this.initialAsset ==null ){
            this.budget = 0L;
            this.initialAsset = 0L;
        }

        if(this.carryOver == null && this.seeProfile == null){
            this.carryOver = Boolean.FALSE;
            this.seeProfile = Boolean.TRUE;
        }

    }

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

    public String getBookKey(){
        return this.bookKey.toString();
    }
}
