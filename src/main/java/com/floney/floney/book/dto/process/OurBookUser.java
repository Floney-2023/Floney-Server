package com.floney.floney.book.dto.process;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
public class OurBookUser {

    private static final String OWNER = "방장";
    private static final String MEMBER = "팀원";

    private final String name;
    private final String profileImg;
    private final String email;
    private boolean isMe;
    private String role;

    @QueryProjection
    public OurBookUser(String name, String profileImg, String email) {
        this.name = name;
        this.profileImg = profileImg;
        this.email = email;
    }

    @Builder
    public OurBookUser(String name, String profileImg, boolean isMe, String role, String email) {
        this.name = name;
        this.profileImg = profileImg;
        this.isMe = isMe;
        this.email = email;
        this.role = role;
    }


    public void checkRole(String providerEmail) {
        if (Objects.equals(this.email, providerEmail)) {
            this.role = OWNER;
        } else {
            this.role = MEMBER;
        }
    }

    public void isMyAccount(String email) {
        this.isMe = Objects.equals(this.email, email);
    }
}
