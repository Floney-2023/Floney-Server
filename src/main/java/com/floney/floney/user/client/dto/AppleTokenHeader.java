package com.floney.floney.user.client.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppleTokenHeader {

    private String alg;
    private String kid;
}
