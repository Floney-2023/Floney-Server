package com.floney.floney.user.entity;

import com.floney.floney.book.dto.request.SaveRecentBookKeyRequest;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.user.dto.constant.Provider;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Entity
@Table(indexes = {
    @Index(name = "email", columnList = "email", unique = true)
})
@DynamicInsert
@DynamicUpdate
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    private static final String DELETE_VALUE = "admin";

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 100)
    private String password;

    @Column(length = 300)
    private String profileImg;

    @Column(nullable = false)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Builder.Default
    private LocalDateTime lastAdTime = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    @ColumnDefault("0")
    private boolean subscribe;

    @Column(nullable = false, updatable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(updatable = false, unique = true)
    private String providerId;

    private String recentBookKey;

    @Column(nullable = false)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Builder.Default
    private LocalDateTime lastLoginTime = LocalDateTime.now();

    @QueryProjection
    private User(String email,
                 String nickname,
                 String password,
                 String profileImg,
                 LocalDateTime lastAdTime,
                 boolean subscribe,
                 Provider provider,
                 String providerId,
                 String recentBookKey,
                 LocalDateTime lastLoginTime) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImg = profileImg;
        this.lastAdTime = lastAdTime;
        this.subscribe = subscribe;
        this.provider = provider;
        this.providerId = providerId;
        this.recentBookKey = recentBookKey;
        this.lastLoginTime = lastLoginTime;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void saveRecentBookKey(SaveRecentBookKeyRequest request) {
        this.recentBookKey = request.getBookKey();
    }

    public void saveDefaultBookKey(String bookKey) {
        this.recentBookKey = bookKey;
    }

    public void login() {
        this.lastLoginTime = LocalDateTime.now();
    }

    public void subscribe() {
        this.subscribe = true;
    }

    public void unSubscribe() {
        this.subscribe = false;
    }
}
