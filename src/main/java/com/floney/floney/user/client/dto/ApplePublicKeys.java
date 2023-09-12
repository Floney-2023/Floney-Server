package com.floney.floney.user.client.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplePublicKeys {

    private List<ApplePublicKey> keys;
}
