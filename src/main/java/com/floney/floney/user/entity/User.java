package com.floney.floney.user.entity;

import com.floney.floney.common.BaseEntity;
import com.floney.floney.user.dto.constant.Provider;
import java.time.LocalDateTime;
import javax.persistence.*;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(nullable = false, updatable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 100)
    private String password;

    @Column(length = 300)
    private String profileImg;

    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private boolean marketingAgree;

    @Column(nullable = false)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private LocalDateTime lastAdTime;

    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    @ColumnDefault("0")
    private boolean subscribe;

    @Column(nullable = false, updatable = false, length = 10)
    private String provider;

    @Builder(builderMethodName = "signupBuilder")
    private User(String email, String nickname, String password, boolean marketingAgree, Provider provider) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.marketingAgree = marketingAgree;
        this.lastAdTime = LocalDateTime.now();
        this.provider = provider.getName();
    }

    @Builder
    private User(String email, String nickname, String password, String profileImg, boolean marketingAgree,
                LocalDateTime lastAdTime, boolean subscribe, Provider provider) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImg = profileImg;
        this.marketingAgree = marketingAgree;
        this.lastAdTime = lastAdTime;
        this.subscribe = subscribe;
        this.provider = provider.getName();
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void signout() {
        this.status = false;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public boolean isSubscribe(){
        return subscribe;
    }
    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
}
