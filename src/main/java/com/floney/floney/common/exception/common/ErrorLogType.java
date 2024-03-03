package com.floney.floney.common.exception.common;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorLogType {
    USER_FOUND(ErrorLogLevel.WARN, "이미 존재하는 유저: [%s]"),
    INVALID_MAIL_ADDRESS(ErrorLogLevel.WARN, "유효하지 않은 메일 주소: [%s]"),
    FAIL_TO_SEND_MAIL(ErrorLogLevel.ERROR, "메일 전송 오류 \n [ERROR_MSG] : {} \n [ERROR STACK] : {}"),
    EXPIRED_JWT_TOKEN(ErrorLogLevel.WARN, "만료된 토큰"),
    INVALID_JWT_TOKEN(ErrorLogLevel.WARN, "유효하지 않은 JWT 토큰: %s"),
    USER_NOT_FOUND(ErrorLogLevel.WARN, "저장되지 않은 유저 정보: [%s]"),
    EMAIL_NOT_FOUND(ErrorLogLevel.WARN, "이메일(%s) 찾기 실패"),
    INVALID_CODE(ErrorLogLevel.DEBUG, "일치하지 않는 이메일 인증 코드: [%s], [%s]"),
    INVALID_OAUTH_RESPONSE(ErrorLogLevel.ERROR, "외부 로그인 서버에서 빈 응답을 받음 \n [ERROR_MSG] : {} \n [ERROR STACK] : {}"),
    INVALID_OAUTH_TOKEN(ErrorLogLevel.WARN, "외부 로그인 서버에서 잘못된 토큰으로 인한 인증 실패"),
    SAME_PASSWORD(ErrorLogLevel.NONE, null),
    CODE_NOT_FOUND(ErrorLogLevel.DEBUG, "이메일[%s] 인증 시간 만료"),
    EMPTY_SIGNOUT_OTHER_REASON(ErrorLogLevel.WARN, "회원 탈퇴 요청에서 value가 비어있음"),
    NOT_EMAIL_USER(ErrorLogLevel.WARN, "이메일 유저가 아니라 간편 유저(%s)임"),
    NOT_SAME_PASSWORD(ErrorLogLevel.NONE, null),

    NOT_FOUND_BOOK(ErrorLogLevel.WARN, "가계부 키 [%s] 가계부 %s"),
    MAX_MEMBER(ErrorLogLevel.WARN, "가계부 키 [%s] 가계부 %s => 현 인원[%s]"),
    NOT_FOUND_CATEGORY(ErrorLogLevel.WARN, "[{}] {}"),
    NO_DELETE_BOOK(ErrorLogLevel.NONE, null),
    NO_AUTHORITY( ErrorLogLevel.WARN,"%s : 가계부 방장 [%s] , 요청자 [%s]"),
    NOT_FOUND_BOOK_USER(ErrorLogLevel.WARN, "가계부 키 [%s] 에서 [%s]와 일치하는 %s"),
    NOT_FOUND_BOOK_LINE(ErrorLogLevel.NONE, null),
    ALREADY_JOIN(ErrorLogLevel.WARN, "{}는 {} \n [ERROR_MSG] : {} \n  [ERROR_STACK] : {}"),
    NOT_FOUND_ALARM(ErrorLogLevel.ERROR, "{} id = {} \n [ERROR_MSG] : {} \n [ERROR_STACK] : {}"),
    ALREADY_EXIST(ErrorLogLevel.WARN, "%s data = %s"),
    CANNOT_ANALYZE(ErrorLogLevel.WARN, "분석이 불가능한 카테고리: %s"),

    LIMIT(ErrorLogLevel.WARN, null),

    NOT_FOUND_SETTLEMENT(ErrorLogLevel.WARN, "settlement id [%s]의 정산 내역이 존재하지 않음"),
    NOT_FOUND_OUTCOME_USER(ErrorLogLevel.WARN, "Request Body에서 지출 내역의 유저가 유저 목록에 존재하지 않음"),

    FAIL_TO_GENERATE_TOKEN(ErrorLogLevel.NONE, null),

    SERVER_ERROR(ErrorLogLevel.ERROR, null),
    REQUEST_BODY_ERROR(ErrorLogLevel.WARN, "요청 body의 @Valid 에러 - [{}] : {} "),
    REQUEST_PARAMETER_ERROR(ErrorLogLevel.WARN, "요청 파라미터 없음 : {} ");

    private final ErrorLogLevel level;
    private final String pattern;

    public String generateLogMessage(final String... attributes) {
        if (ErrorLogLevel.NONE.equals(this.level)) {
            return null;
        }
        return String.format(this.pattern, (Object[]) attributes);
    }

    public ErrorLogLevel getLevel() {
        return this.level;
    }

}
