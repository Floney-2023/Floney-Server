package com.floney.floney.common;

import com.floney.floney.common.exception.MailAddressException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailProvider {

    private final JavaMailSender javaMailSender;

    public void sendMail(String email, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);

        try{
            javaMailSender.send(simpleMailMessage);
        } catch (MailParseException exception) {
            throw new MailAddressException();
        }
    }

}
