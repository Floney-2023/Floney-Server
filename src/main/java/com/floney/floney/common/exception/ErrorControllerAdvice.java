package com.floney.floney.common.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorControllerAdvice {

    // USER
    @ExceptionHandler(UserFoundException.class)
    protected ResponseEntity<Map<String, Object>> foundUser(UserFoundException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", exception.getErrorType().getCode());
        body.put("message", exception.getErrorType().getMessage());
        body.put("provider", exception.getProvider());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(MailAddressException.class)
    protected ResponseEntity<ErrorResponse> notValidMailAddress(MailAddressException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(MailSendException.class)
    protected ResponseEntity<ErrorResponse> failToSendMail(MailSendException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ErrorType.FAIL_TO_SEND_MAIL));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<ErrorResponse> expiredJwtToken(ExpiredJwtException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(ErrorType.EXPIRED_JWT_TOKEN));
    }

    @ExceptionHandler(JwtException.class)
    protected ResponseEntity<ErrorResponse> invalidJwtToken(JwtException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(ErrorType.INVALID_JWT_TOKEN));
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ErrorResponse> invalidLogin(BadCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(ErrorType.INVALID_LOGIN));
    }

    // BOOK
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

    @ExceptionHandler(NotFoundCategoryException.class)
    protected ResponseEntity<ErrorResponse> notFoundCategory(NotFoundCategoryException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(OutOfBudgetException.class)
    protected ResponseEntity<ErrorResponse> outOfBudget(OutOfBudgetException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(exception.getErrorType()));
    }
}

