package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorLogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MaxMemberException extends FloneyException {

    public MaxMemberException(final String bookKey, final int memberCount) {
        super(ErrorType.MAX_MEMBER,
                HttpStatus.BAD_REQUEST,
                ErrorLogType.MAX_MEMBER, bookKey, ErrorType.MAX_MEMBER.getMessage(), String.valueOf(memberCount));
    }
}
