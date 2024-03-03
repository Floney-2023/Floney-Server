package com.floney.floney.common.exception.common;

import com.floney.floney.common.exception.book.*;
import com.floney.floney.common.exception.user.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    // USER
    @ExceptionHandler(UserFoundException.class)
    protected ResponseEntity<ErrorResponse> foundUser(UserFoundException exception) {
        return createResponse(exception, Map.of("provider", exception.getProvider()));
    }

    @ExceptionHandler(FloneyException.class)
    protected ResponseEntity<ErrorResponse> floney(FloneyException exception) {
        return createResponse(exception, null);
    }

    @ExceptionHandler(MailException.class)
    protected ResponseEntity<ErrorResponse> failToSendMail(MailException exception) {
        logger.error("메일 전송 오류 \n [ERROR_MSG] : {} \n [ERROR STACK] : {}", exception.getMessage(), exception.getStackTrace());
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

    @ExceptionHandler(OAuthResponseException.class)
    protected ResponseEntity<ErrorResponse> badOAuthResponse(OAuthResponseException exception) {
        logger.error("외부 로그인 서버에서 빈 응답을 받음 \n [ERROR_MSG] : {} \n [ERROR STACK] : {}", exception.getMessage(), exception.getStackTrace());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of(exception.getErrorType()));
    }

    // BOOK
    @ExceptionHandler(NotFoundCategoryException.class)
    protected ResponseEntity<ErrorResponse> notFoundCategory(NotFoundCategoryException exception) {
        logger.warn("[{}] {}", exception.getCategoryName(), ErrorType.NOT_FOUND_CATEGORY.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(ExcelMakingException.class)
    protected ResponseEntity<ErrorResponse> excelError(ExcelMakingException exception) {
        logger.error("엑셀 오류 발생 [ERROR_MSG] : {} \n [ERROR_STACK] : {} \n ", exception.getMessage(), exception.getStackTrace());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(LimitRequestException.class)
    protected ResponseEntity<ErrorResponse> limitOfService(LimitRequestException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(AlreadyJoinException.class)
    protected ResponseEntity<ErrorResponse> joinException(AlreadyJoinException exception) {
        logger.warn("{}는 {} \n [ERROR_MSG] : {} \n  [ERROR_STACK] : {}", exception.getUserEmail(), ErrorType.ALREADY_JOIN.getMessage(), exception.getMessage(), exception.getStackTrace());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(NotFoundAlarmException.class)
    protected ResponseEntity<ErrorResponse> alarmException(NotFoundAlarmException exception) {
        logger.error("{} id = {} \n [ERROR_MSG] : {} \n [ERROR_STACK] : {}", ErrorType.NOT_FOUND_ALARM.getMessage(), exception.getRequestKey(), exception.getMessage(), exception.getStackTrace());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(NotFoundParentCategoryException.class)
    protected ResponseEntity<ErrorResponse> notFoundParentCategoryException(NotFoundParentCategoryException exception) {
        logger.warn("[{}] {}", exception.getCategoryName(), exception.getErrorType().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    // SETTLEMENT

    // OTHER
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> validationException(MethodArgumentNotValidException exception) {
        final String message = exception.getBindingResult().getFieldError().getDefaultMessage();
        logger.warn("요청 body의 @Valid 에러 - [{}] : {} ", exception.getTarget(), message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(ErrorType.REQUEST_BODY_ERROR, message));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> requestParameterException(MissingServletRequestParameterException exception) {
        logger.warn("요청 파라미터 없음 : {} ", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(ErrorType.REQUEST_PARAMETER_ERROR, exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> otherException(Exception exception) {
        loggingError(exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of(ErrorType.SERVER_ERROR));
    }

    private void loggingError(final Exception exception) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(exception).append('\n');

        for (final StackTraceElement info : exception.getStackTrace()) {
            if ("invoke".equals(info.getMethodName())) { // 프록시 메서드 제외
                continue;
            }
            if ("<generated>".equals(info.getFileName())) { // 스프링이 만든 프록시 객체 제외
                continue;
            }

            stringBuilder.append("- ").append(info).append('\n');
        }

        logger.error(stringBuilder.toString());
    }

    private ResponseEntity<ErrorResponse> createResponse(FloneyException exception, Map<String, Object> attributes) {
        ErrorResponse response = ErrorResponse.of(exception.getErrorType(), attributes);
        return ResponseEntity.status(exception.getHttpStatus()).body(response);
    }

}

