package com.floney.floney.common.exception.common;

import com.floney.floney.common.exception.alarm.GoogleAccessTokenGenerateException;
import com.floney.floney.common.exception.book.AlreadyJoinException;
import com.floney.floney.common.exception.book.CannotDeleteBookException;
import com.floney.floney.common.exception.book.ExcelMakingException;
import com.floney.floney.common.exception.book.LimitRequestException;
import com.floney.floney.common.exception.book.MaxMemberException;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundBookLineException;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import com.floney.floney.common.exception.user.CodeNotSameException;
import com.floney.floney.common.exception.user.EmailNotFoundException;
import com.floney.floney.common.exception.user.MailAddressException;
import com.floney.floney.common.exception.user.NotFoundSubscribeException;
import com.floney.floney.common.exception.user.OAuthResponseException;
import com.floney.floney.common.exception.user.OAuthTokenNotValidException;
import com.floney.floney.common.exception.user.UserFoundException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.common.exception.user.UserSignoutException;
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

        logger.warn("이미 존재하는 유저: [{}]", exception.getEmail());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<ErrorResponse> notFoundUser(UserNotFoundException exception) {
        logger.warn("저장되지 않은 유저 정보: [{}]", exception.getUsername());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(UserSignoutException.class)
    protected ResponseEntity<ErrorResponse> signoutUser(UserSignoutException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(MailAddressException.class)
    protected ResponseEntity<ErrorResponse> notValidMailAddress(MailAddressException exception) {
        logger.warn("유효하지 않은 메일 주소: [{}]", exception.getEmail());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(MailException.class)
    protected ResponseEntity<ErrorResponse> failToSendMail(MailException exception) {
        logger.error("메일 전송 오류: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ErrorType.FAIL_TO_SEND_MAIL));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<ErrorResponse> expiredJwtToken(ExpiredJwtException exception) {
        logger.warn("만료된 토큰");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(ErrorType.EXPIRED_JWT_TOKEN));
    }

    @ExceptionHandler(JwtException.class)
    protected ResponseEntity<ErrorResponse> invalidJwtToken(JwtException exception) {
        logger.warn("유효하지 않은 JWT 토큰: {}", exception.getMessage());
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
        logger.warn("이메일({}) 찾기 실패", exception.getEmail());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(CodeNotSameException.class)
    protected ResponseEntity<ErrorResponse> invalidCode(CodeNotSameException exception) {
        logger.debug("일치하지 않는 이메일 인증 코드: [{}], [{}]", exception.getCode(), exception.getAnotherCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(OAuthResponseException.class)
    protected ResponseEntity<ErrorResponse> badOAuthResponse(OAuthResponseException exception) {
        logger.error("외부 로그인 서버에서 빈 응답을 받음");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(OAuthTokenNotValidException.class)
    protected ResponseEntity<ErrorResponse> invalidOAuthToken(OAuthTokenNotValidException exception) {
        logger.warn("외부 로그인 서버에서 잘못된 토큰으로 인한 인증 실패");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    // BOOK
    @ExceptionHandler(NotFoundBookException.class)
    protected ResponseEntity<ErrorResponse> notFoundBook(NotFoundBookException exception) {
        logger.warn("가계부 키 [{}] 가계부 {}", exception.getRequestKey(), ErrorType.NOT_FOUND_BOOK.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(MaxMemberException.class)
    protected ResponseEntity<ErrorResponse> maxMember(MaxMemberException exception) {
        logger.warn("가계부 키 [{}] 가계부 {} => 현 인원[{}]", exception.getBookKey(),
                ErrorType.MAX_MEMBER.getMessage(),
                exception.getMemberCount());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(NotFoundCategoryException.class)
    protected ResponseEntity<ErrorResponse> notFoundCategory(NotFoundCategoryException exception) {
        logger.warn("[{}] {}", exception.getCategoryName(), ErrorType.NOT_FOUND_CATEGORY.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(CannotDeleteBookException.class)
    protected ResponseEntity<ErrorResponse> cannotDeleteBook(CannotDeleteBookException exception) {
        logger.warn("[{}]명의 {}", exception.getLeftMemberCount(), ErrorType.NO_DELETE_BOOK.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(NotFoundBookUserException.class)
    protected ResponseEntity<ErrorResponse> notFoundUser(NotFoundBookUserException exception) {
        logger.warn("가계부 키 [{}] 에서 [{}]와 일치하는 {}",
                exception.getBookKey(),
                exception.getRequestUser(),
                ErrorType.NOT_FOUND_BOOK_USER.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(NotFoundBookLineException.class)
    protected ResponseEntity<ErrorResponse> notFoundLine(NotFoundBookLineException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(ExcelMakingException.class)
    protected ResponseEntity<ErrorResponse> notFoundLine(ExcelMakingException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    //SUBSCRIBE
    @ExceptionHandler(NotSubscribeException.class)
    protected ResponseEntity<ErrorResponse> notSubscribe(NotSubscribeException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(LimitRequestException.class)
    protected ResponseEntity<ErrorResponse> limitOfService(LimitRequestException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(NoAuthorityException.class)
    protected ResponseEntity<ErrorResponse> notOwner(NoAuthorityException exception) {
        logger.warn("{} : 가계부 방장 [{}] , 요청자 [{}]",
                exception.getOwner(),
                ErrorType.NO_AUTHORITY.getMessage(),
                exception.getRequestUser());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(AlreadyJoinException.class)
    protected ResponseEntity<ErrorResponse> joinException(AlreadyJoinException exception) {
        logger.error("{}는 {}",exception.getUserEmail(),ErrorType.ALREADY_JOIN.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(NotFoundSubscribeException.class)
    protected ResponseEntity<ErrorResponse> subscribeException(NotFoundSubscribeException exception) {
        logger.error("{} User = {}",ErrorType.NOT_FOUND_SUBSCRIBE.getMessage(),exception.getUserEmail());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(exception.getErrorType()));
    }

    // ALARM
    @ExceptionHandler(GoogleAccessTokenGenerateException.class)
    protected ResponseEntity<ErrorResponse> failToGenerateGoogleAccessTokenException(GoogleAccessTokenGenerateException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> otherException(Exception exception) {
        logger.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ErrorType.SERVER_ERROR));
    }
}

