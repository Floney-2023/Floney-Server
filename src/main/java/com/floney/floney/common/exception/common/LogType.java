package com.floney.floney.common.exception.common;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum LogType {
    USER_FOUND(CustomLogLevel.WARN, "이미 존재하는 유저: [%s]"),
    INVALID_MAIL_ADDRESS(CustomLogLevel.WARN, "유효하지 않은 메일 주소: [%s]"),
    FAIL_TO_SEND_MAIL(CustomLogLevel.ERROR, "메일 전송 오류 \n [ERROR_MSG] : {} \n [ERROR STACK] : {}"),
    EXPIRED_JWT_TOKEN(CustomLogLevel.WARN, "만료된 토큰"),
    INVALID_JWT_TOKEN(CustomLogLevel.WARN, "유효하지 않은 JWT 토큰: %s"),
    USER_NOT_FOUND(CustomLogLevel.WARN, "저장되지 않은 유저 정보: [%s]"),
    EMAIL_NOT_FOUND(CustomLogLevel.WARN, "이메일(%s) 찾기 실패"),
    INVALID_CODE(CustomLogLevel.DEBUG, "일치하지 않는 이메일 인증 코드: [%s], [%s]"),
    INVALID_OAUTH_RESPONSE(CustomLogLevel.ERROR, "외부 로그인 서버에서 빈 응답을 받음 \n [ERROR_MSG] : {} \n [ERROR STACK] : {}"),
    INVALID_OAUTH_TOKEN(CustomLogLevel.WARN, "외부 로그인 서버에서 잘못된 토큰으로 인한 인증 실패"),
    SAME_PASSWORD(CustomLogLevel.NONE, null),
    CODE_NOT_FOUND(CustomLogLevel.DEBUG, "이메일[%s] 인증 시간 만료"),
    EMPTY_SIGNOUT_OTHER_REASON(CustomLogLevel.WARN, "회원 탈퇴 요청에서 value가 비어있음"),
    NOT_EMAIL_USER(CustomLogLevel.WARN, "이메일 유저가 아니라 간편 유저(%s)임"),
    NOT_SAME_PASSWORD(CustomLogLevel.NONE, null),

    NOT_FOUND_BOOK(CustomLogLevel.WARN, "가계부 키 [%s] 가계부 %s"),
    MAX_MEMBER(CustomLogLevel.WARN, "가계부 키 [%s] 가계부 %s => 현 인원[%s]"),
    NOT_FOUND_CATEGORY(CustomLogLevel.WARN, "[{}] {}"),
    NO_DELETE_BOOK(CustomLogLevel.NONE, null),
    NO_AUTHORITY( CustomLogLevel.WARN,"%s : 가계부 방장 [%s] , 요청자 [%s]"),
    NOT_FOUND_BOOK_USER(CustomLogLevel.WARN, "가계부 키 [%s] 에서 [%s]와 일치하는 %s"),
    NOT_FOUND_BOOK_LINE(CustomLogLevel.NONE, null),
    ALREADY_JOIN(CustomLogLevel.WARN, "{}는 {} \n [ERROR_MSG] : {} \n  [ERROR_STACK] : {}"),
    NOT_FOUND_ALARM(CustomLogLevel.ERROR, "{} id = {} \n [ERROR_MSG] : {} \n [ERROR_STACK] : {}"),
    ALREADY_EXIST(CustomLogLevel.WARN, "%s data = %s"),
    CANNOT_ANALYZE(CustomLogLevel.WARN, "분석이 불가능한 카테고리: %s"),

    LIMIT(CustomLogLevel.WARN, null),

    NOT_FOUND_SETTLEMENT(CustomLogLevel.WARN, "settlement id [%s]의 정산 내역이 존재하지 않음"),
    NOT_FOUND_OUTCOME_USER(CustomLogLevel.WARN, "Request Body에서 지출 내역의 유저가 유저 목록에 존재하지 않음"),

    FAIL_TO_GENERATE_TOKEN(CustomLogLevel.NONE, null),

    SERVER_ERROR(CustomLogLevel.ERROR, null),
    REQUEST_BODY_ERROR(CustomLogLevel.WARN, "요청 body의 @Valid 에러 - [{}] : {} "),
    REQUEST_PARAMETER_ERROR(CustomLogLevel.WARN, "요청 파라미터 없음 : {} ");

    private final CustomLogLevel level;
    private final String pattern;

    public String generateLog(String... attributes) {
        if (CustomLogLevel.NONE.equals(this.level)) {
            return null;
        }
        return String.format(this.pattern, (Object[]) attributes);
    }

    public CustomLogLevel getLevel() {
        return this.level;
    }

}
