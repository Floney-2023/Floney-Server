package com.floney.floney.user.domain.vo;

import com.floney.floney.common.domain.vo.Mail;

public class RegeneratePasswordMail extends Mail {

    private static final String SUBJECT = "[Floney] 새 비밀번호 안내";
    private static final String CONTENT = "새 비밀번호: %s\n바뀐 비밀번호로 로그인 해주세요.\n";

    private RegeneratePasswordMail(final String email, final String subject, final String content) {
        super(email, subject, content);
    }

    public static RegeneratePasswordMail create(final String email, final String newPassword) {
        return new RegeneratePasswordMail(email, SUBJECT, String.format(CONTENT, newPassword));
    }
}
