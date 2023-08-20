package com.floney.floney.common.exception.common;

import com.floney.floney.common.exception.book.*;
import com.floney.floney.common.exception.user.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorControllerAdvice {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

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

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<ErrorResponse> notFoundUsername(UserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<ErrorResponse> notFoundUser(UserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(MailAddressException.class)
    protected ResponseEntity<ErrorResponse> notValidMailAddress(MailAddressException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(MailException.class)
    protected ResponseEntity<ErrorResponse> failToSendMail(MailException exception) {
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

    @ExceptionHandler(EmailNotFoundException.class)
    protected ResponseEntity<ErrorResponse> emailNotFound(EmailNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(CodeNotSameException.class)
    protected ResponseEntity<ErrorResponse> invalidCode(CodeNotSameException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(EmailNotValidException.class)
    protected ResponseEntity<ErrorResponse> invalidEmail(EmailNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(OAuthResponseException.class)
    protected ResponseEntity<ErrorResponse> badOAuthResponse(OAuthResponseException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(OAuthTokenNotValidException.class)
    protected ResponseEntity<ErrorResponse> invalidOAuthToken(OAuthTokenNotValidException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    // BOOK
    @ExceptionHandler(NotFoundBookException.class)
    protected ResponseEntity<ErrorResponse> notFoundBook(NotFoundBookException exception) {
        logger.warn("가계부 키= [{}] 가계부 {}",exception.getRequestKey(),ErrorType.NOT_FOUND_BOOK.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(MaxMemberException.class)
    protected ResponseEntity<ErrorResponse> maxMember(MaxMemberException exception) {
        logger.warn("가계부 키= [{}] 가계부 {} => 현 인원[{}]",exception.getBookKey(),ErrorType.MAX_MEMBER.getMessage(),exception.getMemberCount());
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

    @ExceptionHandler(NotFoundBookUserException.class)
    protected ResponseEntity<ErrorResponse> notFoundUser(NotFoundBookUserException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(exception.getErrorType()));
    }

    //SUBSCRIBE
    @ExceptionHandler(NotSubscribeException.class)
    protected ResponseEntity<ErrorResponse> notSubscribe(NotSubscribeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(LimitRequestException.class)
    protected ResponseEntity<ErrorResponse> limitOfService(LimitRequestException exception) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(NoAuthorityException.class)
    protected ResponseEntity<ErrorResponse> notOwner(NoAuthorityException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse.of(exception.getErrorType()));
    }
}

