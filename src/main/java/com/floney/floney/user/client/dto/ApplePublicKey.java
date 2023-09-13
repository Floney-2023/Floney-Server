package com.floney.floney.user.client.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplePublicKey {

    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;
}
