package com.floney.floney.book.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

public class OurBookUser {
    private String name;
    private String profileImg;
    private boolean isMe;
    private String role;

    @QueryProjection
    public OurBookUser(String name, String profileImg) {
        this.name = name;
        this.profileImg = profileImg;
    }

    @Builder
    public OurBookUser(String name, String profileImg, boolean isMe, String role) {
        this.name = name;
        this.profileImg = profileImg;
        this.isMe = isMe;
        this.role = role;
    }



}
