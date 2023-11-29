package com.floney.floney.user.dto.response;

import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignoutResponse {

    private List<String> deletedBookKeys;
    private List<String> otherBookKeys;

    public static SignoutResponse of(final List<String> deletedBookKeys,
                                     final List<String> otherBookKeys) {
        return new SignoutResponse(deletedBookKeys, otherBookKeys);
    }
}
