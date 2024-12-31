package com.floney.floney.subscribe.dto;

import lombok.Getter;

@Getter
public class IsSubscribeBookResponse {
    public boolean maxFavorite;

    public boolean overBookUser;

    public IsSubscribeBookResponse(boolean maxFavorite, boolean overBookUser) {
        this.overBookUser = overBookUser;
        this.maxFavorite = maxFavorite;
    }

}
