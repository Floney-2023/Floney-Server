package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MaxMemberException extends FloneyException {

    public MaxMemberException(final String bookKey, final int memberCount) {
        super(ErrorType.MAX_MEMBER, HttpStatus.BAD_REQUEST);
        logger.warn("가계부 키 [{}] 가계부 {} => 현 인원[{}]", bookKey, errorType.getMessage(), memberCount);
    }
}
