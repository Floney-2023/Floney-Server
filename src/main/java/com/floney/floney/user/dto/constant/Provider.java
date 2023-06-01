package com.floney.floney.user.dto.constant;

import com.floney.floney.common.exception.ProviderNotFoundException;
import java.util.Arrays;
import lombok.Getter;

public enum Provider {
    EMAIL("email"),
    KAKAO("kakao"),
    GOOGLE("google"),
    APPLE("apple");

    @Getter
    private final String name;

    Provider(String name) {
        this.name = name;
    }

    public static Provider findByName(String name) {
        return Arrays.stream(Provider.values())
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(ProviderNotFoundException::new);
    }

}
