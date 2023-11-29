package com.floney.floney.user.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserSignedOutEvent {

    private final long userId;
}
