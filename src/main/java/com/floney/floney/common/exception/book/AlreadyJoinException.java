package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AlreadyJoinException extends FloneyException {

    private final String userEmail;

    public AlreadyJoinException(String userEmail) {
        super(ErrorType.ALREADY_JOIN, HttpStatus.BAD_REQUEST);
        this.userEmail = userEmail;
    }

}
