package com.floney.floney.user.entity;

import com.floney.floney.common.BaseEntity;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String email;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 100)
    private String password;

    @Column(length = 300)
    private String profileImg;

    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private int marketingAgree;

    @Column
    private LocalDateTime lastAdTime;

    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    @ColumnDefault("0")
    private int subscribe;

    @Column(nullable = false, length = 10)
    private String provider;

    private User(String nickname, String email, String password, String profileImg, int marketingAgree,
                 int subscribe, LocalDateTime lastAdTime, String provider) {

        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImg = profileImg;
        this.marketingAgree = marketingAgree;
        this.subscribe = subscribe;
        this.lastAdTime = lastAdTime;
        this.provider = provider;
    }

    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }

    public static User of(
            String nickname, String email, String password, String profileImg,
            int marketingAgree, int subscribe, LocalDateTime lastAdTime, String provider
    ) {
        return new User(nickname, email, password, profileImg, marketingAgree, subscribe, lastAdTime, provider);
    }

    public void signout() {
        this.status = "inactive";
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
