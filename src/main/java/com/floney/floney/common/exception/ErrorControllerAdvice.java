package com.floney.floney.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorControllerAdvice {

    @ExceptionHandler(NotFoundBookException.class)
    protected ResponseEntity<ErrorResponse> notFoundBook(NotFoundBookException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(MaxMemberException.class)
    protected ResponseEntity<ErrorResponse> maxMember(MaxMemberException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(exception.getErrorType()));
    }
}

