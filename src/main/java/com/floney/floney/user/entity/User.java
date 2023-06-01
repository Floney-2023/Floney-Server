package com.floney.floney.user.entity;

import com.floney.floney.common.BaseEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
    private Boolean marketingAgree;

    @Column(nullable = false)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Builder.Default
    private LocalDateTime lastAdTime = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    @ColumnDefault("0")
    private boolean subscribe;

    @Column(nullable = false, updatable = false, length = 10)
    private String provider;

    @Column(updatable = false, unique = true)
    private Long providerId;

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

    public boolean isSubscribe() {
        return subscribe;
    }

    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

}
