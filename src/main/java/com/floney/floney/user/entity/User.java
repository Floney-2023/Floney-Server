package com.floney.floney.user.entity;

import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.common.exception.user.NotEmailUserException;
import com.floney.floney.common.util.Events;
import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.event.UserSignedOutEvent;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    public static final String DELETE_VALUE = "알수없음";

    private static final int EMAIL_MAX_LENGTH = 350;
    private static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 32;
    private static final int NICKNAME_MAX_LENGTH = 8;

    private static final String DEFAULT_PROFILE_IMG = "user_default";

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String password;

    @Column(length = 300)
    @Builder.Default
    private String profileImg = DEFAULT_PROFILE_IMG;

    @Column(nullable = false)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Builder.Default
    private LocalDateTime lastAdTime = LocalDateTime.now();

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(unique = true)
    private String providerId;

    private String recentBookKey;

    @Column(nullable = false)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Builder.Default
    private LocalDateTime lastLoginTime = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "TINYINT")
    private boolean receiveMarketing;

    @QueryProjection
    private User(String email,
                 String nickname,
                 String password,
                 String profileImg,
                 LocalDateTime lastAdTime,
                 Provider provider,
                 String providerId,
                 String recentBookKey,
                 LocalDateTime lastLoginTime,
                 boolean receiveMarketing) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImg = profileImg;
        this.lastAdTime = lastAdTime;
        this.provider = provider;
        this.providerId = providerId;
        this.recentBookKey = recentBookKey;
        this.lastLoginTime = lastLoginTime;
        this.receiveMarketing = receiveMarketing;
    }

    public static User signupByEmail(final String email,
                                     final String password,
                                     final String nickname,
                                     final Boolean receiveMarketing) {
        validateSignup(email, password, nickname);

        return User.builder()
            .email(email)
            .password(password)
            .nickname(nickname)
            .provider(Provider.EMAIL)
            .receiveMarketing(receiveMarketing)
            .build();
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        validatePassword(password);
        this.password = password;
    }

    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void updateReceiveMarketing(final boolean receiveMarketing) {
        this.receiveMarketing = receiveMarketing;
    }

    public void saveRecentBookKey(String bookKey) {
        this.recentBookKey = bookKey;
    }

    public void saveDefaultBookKey(String bookKey) {
        this.recentBookKey = bookKey;
    }

    public void login() {
        this.lastLoginTime = LocalDateTime.now();
    }

    public void signout() {
        deleteInformation();
        inactive();

        Events.raise(new UserSignedOutEvent(getId()));
    }

    private void deleteInformation() {
        email = DELETE_VALUE;
        password = DELETE_VALUE;
        nickname = DELETE_VALUE;
        profileImg = null;
        providerId = null;
        recentBookKey = null;
    }

    public void validateEmailUser() {
        if (!Provider.EMAIL.equals(provider)) {
            throw new NotEmailUserException(provider);
        }
    }

    private static void validateSignup(final String email, final String password, final String nickname) {
        validateEmail(email);
        validatePassword(password);
        validateNickname(nickname);
    }

    private static void validateEmail(final String email) {
        if (email.isBlank()) {
            throw new RuntimeException("이메일이 비어 있습니다");
        }

        if (email.length() > EMAIL_MAX_LENGTH) {
            throw new RuntimeException("이메일의 길이는 " + EMAIL_MAX_LENGTH + "자를 넘을 수 없습니다");
        }
    }

    private static void validatePassword(final String password) {
        if (password.isBlank()) {
            throw new RuntimeException("비밀번호가 비어 있습니다");
        }

        if (password.length() < PASSWORD_MIN_LENGTH) {
            throw new RuntimeException("비밀번호의 길이는 " + PASSWORD_MIN_LENGTH + "자를 넘어야 합니다");
        }

        if (password.length() > PASSWORD_MAX_LENGTH) {
            throw new RuntimeException("비밀번호의 길이는 " + PASSWORD_MAX_LENGTH + "자를 넘을 수 없습니다");
        }
    }

    private static void validateNickname(final String nickname) {
        if (nickname.isBlank()) {
            throw new RuntimeException("닉네임이 비어 있습니다");
        }

        if (nickname.length() > NICKNAME_MAX_LENGTH) {
            throw new RuntimeException("닉네임의 길이는 " + NICKNAME_MAX_LENGTH + "자를 넘을 수 없습니다");
        }
    }
}
