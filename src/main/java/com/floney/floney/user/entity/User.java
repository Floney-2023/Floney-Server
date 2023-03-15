package com.floney.floney.user.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Getter
@Entity
@NoArgsConstructor
@DynamicInsert
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userIdx;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 300)
    private String profileImg;

    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private int marketingAgree;

    @Column
    private LocalDateTime lastAdTime;

    @Column(nullable = false, length = 10)
    private String provider;

    @Column
    private String refreshToken;
}
