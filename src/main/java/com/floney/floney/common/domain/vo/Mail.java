package com.floney.floney.common.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Mail {

    private final String email;
    private final String subject;
    private final String content;
}
