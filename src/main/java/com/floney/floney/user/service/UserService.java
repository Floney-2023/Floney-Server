package com.floney.floney.user.service;

import com.floney.floney.common.exception.UserSignoutException;
import com.floney.floney.common.token.RedisProvider;
import com.floney.floney.common.exception.MailAddressException;
import com.floney.floney.common.exception.UserFoundException;
import com.floney.floney.common.exception.UserNotFoundException;
import com.floney.floney.common.token.JwtTokenProvider;
import com.floney.floney.common.token.dto.TokenDto;
import com.floney.floney.user.dto.MyPageResponse;
import com.floney.floney.user.dto.UserResponse;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import io.jsonwebtoken.MalformedJwtException;
import java.util.NoSuchElementException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final RedisProvider redisProvider;
    private final JavaMailSender javaMailSender;

    public TokenDto login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        return jwtTokenProvider.generateToken(authentication);
    }

    public String logout(String accessToken) {
        jwtTokenProvider.validateToken(accessToken);
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String email = authentication.getName();

        if (redisProvider.get(email) != null) {
            redisProvider.delete(email);
        }

        long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisProvider.set(accessToken, "logout", expiration);

        return email;
    }

    @Transactional
    public void signup(UserResponse userResponse) {
        try {
            User user = userRepository.findByEmail(userResponse.getEmail()).orElseThrow();
            throw new UserFoundException(user.getProvider());
        } catch (NoSuchElementException exception) {
            User user = userResponse.to();
            user.encodePassword(bCryptPasswordEncoder);

            userRepository.save(user);
        }
    }

    @Transactional
    public void signout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        if(!user.isStatus()) {
            throw new UserSignoutException();
        }

        user.signout();
        userRepository.save(user);
    }

    public TokenDto regenerateToken(TokenDto tokenDto) {
        String accessToken = tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String redisRefreshToken = redisProvider.get(authentication.getName());

        if (!refreshToken.equals(redisRefreshToken)) {
            throw new MalformedJwtException("");
        }

        return jwtTokenProvider.generateToken(authentication);
    }

    public MyPageResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        return MyPageResponse.from(UserResponse.from(user));
    }

    @Transactional
    public void updateNickname(String nickname) {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(UserNotFoundException::new);

        user.updateNickname(nickname);
    }

    @Transactional
    public void updatePassword(String password) {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(UserNotFoundException::new);

        user.updatePassword(password);
        user.encodePassword(bCryptPasswordEncoder);
    }

    @Transactional
    public void updatePassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        user.updatePassword(password);
        user.encodePassword(bCryptPasswordEncoder);
    }

    @Transactional
    public void updateProfileImg(String profileImg) {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(UserNotFoundException::new);

        user.updateProfileImg(profileImg);
    }

    public String sendAuthenticateEmail(String email) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        String code = String.format("%06d", random.nextInt(1_000_000) % 1_000_000);

        String mailSubject = "[Floney] 이메일 인증 코드";
        String mailText = String.format("인증 코드: %s\n앱으로 돌아가서 인증을 완료해주세요.\n", code);

        sendMail(email, mailSubject, mailText);
        return code;
    }

    public String sendPasswordFindEmail(String email) {
        String newPassword = RandomStringUtils.random(50, true, true);

        String mailSubject = "[Floney] 새 비밀번호 안내";
        String mailText = String.format("새 비밀번호: %s\n바뀐 비밀번호로 로그인 해주세요.\n", newPassword);

        sendMail(email, mailSubject, mailText);
        return newPassword;
    }

    private void sendMail(String email, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);

        try{
            javaMailSender.send(simpleMailMessage);
        } catch (MailParseException | MailAuthenticationException exception) {
            throw new MailAddressException();
        }
    }

}
