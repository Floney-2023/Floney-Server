package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;

@Getter
public class MaxMemberException extends FloneyException {

    private static final String LOG_PATTERN = "가계부 키 [%s] 가계부 %s => 현 인원[%s]";

    public MaxMemberException(final String bookKey, final int memberCount) {
        super(ErrorType.MAX_MEMBER, LOG_PATTERN, bookKey, ErrorType.MAX_MEMBER.getMessage(), String.valueOf(memberCount));
    }
}
