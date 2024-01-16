package com.floney.floney.common.exception.common;

import com.floney.floney.common.exception.alarm.GoogleAccessTokenGenerateException;
import com.floney.floney.common.exception.book.*;
import com.floney.floney.common.exception.settlement.OutcomeUserNotFoundException;
import com.floney.floney.common.exception.settlement.SettlementNotFoundException;
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

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(MailAddressException.class)
    protected ResponseEntity<ErrorResponse> notValidMailAddress(MailAddressException exception) {
        logger.warn("유효하지 않은 메일 주소: [{}]", exception.getEmail());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
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

    @ExceptionHandler(CodeNotFoundException.class)
    protected ResponseEntity<ErrorResponse> notExistCode(CodeNotFoundException exception) {
        logger.debug("이메일[{}] 인증 시간 만료", exception.getEmail());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(OAuthResponseException.class)
    protected ResponseEntity<ErrorResponse> badOAuthResponse(OAuthResponseException exception) {
        logger.error("외부 로그인 서버에서 빈 응답을 받음 \n [ERROR_MSG] : {} \n [ERROR STACK] : {}", exception.getMessage(), exception.getStackTrace());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(OAuthTokenNotValidException.class)
    protected ResponseEntity<ErrorResponse> invalidOAuthToken(OAuthTokenNotValidException exception) {
        logger.warn("외부 로그인 서버에서 잘못된 토큰으로 인한 인증 실패");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(SignoutOtherReasonEmptyException.class)
    protected ResponseEntity<ErrorResponse> signoutOtherReasonEmpty(SignoutOtherReasonEmptyException exception) {
        logger.warn("회원 탈퇴 요청에서 value가 비어있음");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(NotEmailUserException.class)
    protected ResponseEntity<ErrorResponse> notEmailUser(NotEmailUserException exception) {
        logger.warn("이메일 유저가 아니라 간편 유저({})임", exception.getProvider());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(PasswordSameException.class)
    protected ResponseEntity<ErrorResponse> samePassword(PasswordSameException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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

    @ExceptionHandler(AlreadyExistException.class)
    protected ResponseEntity<ErrorResponse> alreadyExistException(AlreadyExistException exception) {
        logger.warn("{} data = {}", ErrorType.ALREADY_EXIST.getMessage(), exception.getTarget());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    // SETTLEMENT
    @ExceptionHandler(SettlementNotFoundException.class)
    protected ResponseEntity<ErrorResponse> settlementNotFoundException(SettlementNotFoundException exception) {
        logger.warn("settlement id [{}]의 정산 내역이 존재하지 않음", exception.getSettlementId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    @ExceptionHandler(OutcomeUserNotFoundException.class)
    protected ResponseEntity<ErrorResponse> outcomeUserNotFoundException(OutcomeUserNotFoundException exception) {
        logger.warn("Request Body에서 지출 내역의 유저가 유저 목록에 존재하지 않음");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

    // ALARM
    @ExceptionHandler(GoogleAccessTokenGenerateException.class)
    protected ResponseEntity<ErrorResponse> failToGenerateGoogleAccessTokenException(GoogleAccessTokenGenerateException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(exception.getErrorType()));
    }

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
            if (!info.getClassName().startsWith("com.floney")) { // 프로젝트 내에서 발생한 에러만 포함
                continue;
            }
            if ("invoke".equals(info.getMethodName())) { // 프록시 메서드 제외
                continue;
            }
            if ("<generated>".equals(info.getFileName())) { // 스프링이 만든 프록시 객체 제외
                continue;
            }

            stringBuilder.append("\t- ").append(info).append('\n');
        }

        logger.error(stringBuilder.toString());
    }
}

